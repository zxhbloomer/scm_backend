# AI工作流智能路由集成方案 - Spring AI标准版

## 📋 文档信息

**创建时间**: 2025-11-10
**方案版本**: v2.0 (Spring AI标准版)
**状态**: 待审批
**作者**: SCM-AI团队

---

## 🎯 需求概述

### 核心需求
在现有的AI对话系统中集成工作流路由和意图识别功能，实现：
1. 在 `AiConversationController.chatStream()` 中插入路由逻辑
2. 支持工作流的多轮对话（暂停/恢复机制）
3. 判断可用工作流：用户自己的（已发布）非公开工作流 + 公开工作流（去重）

### 典型场景
```
场景1: 首次工作流调用
User: "帮我查询订单"
  → 路由到订单查询工作流
  → 工作流返回: "请提供订单号"

场景2: 多轮对话 - 继续工作流
User: "ORD-20251110-001"
  → 从对话历史识别上下文
  → 继续执行同一工作流
  → 返回订单查询结果
```

---

## 🔬 调研结论

### Spring AI官方文档调研

根据Spring AI官方文档 (https://github.com/spring-projects/spring-ai)，标准的对话扩展方式是：

1. **使用Advisor机制**：
   - Spring AI提供 `CallAdvisor` 和 `StreamAdvisor` 接口
   - Advisor可以在ChatClient调用链中插入自定义逻辑
   - 多个Advisor按Order顺序执行，形成责任链

2. **对话记忆管理**：
   - 使用 `MessageChatMemoryAdvisor` 自动管理对话历史
   - 通过 `conversationId` 隔离不同会话
   - 支持多种存储后端（内存、JDBC、Redis、MongoDB等）

3. **流式响应**：
   - ChatClient支持 `stream().chatResponse()` 返回 `Flux<ChatClientResponse>`
   - 符合Spring WebFlux的Reactive编程范式

### 现有WorkflowEngine调研

通过源码分析发现：

1. **执行机制**：
   - 基于LangGraph4j的DAG执行引擎
   - 通过 `WorkflowStreamHandler` 回调发送事件
   - `streaming()` 使用 `Flux.create()` 将回调转换为Flux

2. **暂停/恢复机制**：
   - 工作流等待用户输入时，触发 `onNodeWaitFeedback` 事件
   - WorkflowEngine实例存储在 `InterruptedFlow.RUNTIME_TO_GRAPH` 缓存（10分钟TTL）
   - 调用 `resumeFlow(runtimeUuid, userInput)` 恢复执行

3. **存在的问题**：
   - `resumeFlow()` 是同步void方法，无法直接返回Flux
   - 需要为其创建流式版本 `resumeFlowAsFlux()`

---

## 🏗️ 架构设计

### 总体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端 (Vue.js)                             │
│                    SSE流式接收响应                                │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTP POST /chat/stream
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│            AiConversationController.chatStream()                │
│  1. 保存用户消息到ai_conversation_content                        │
│  2. 调用ChatClient.prompt().stream().chatResponse()             │
│  3. 转换Flux<ChatClientResponse> → Flux<ChatResponseVo>         │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                      ChatClient (Spring AI)                     │
│              .advisors(a -> a.param(CONVERSATION_ID, id))       │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓ Advisor责任链
┌─────────────────────────────────────────────────────────────────┐
│  🔹 WorkflowContextAdvisor (Order=0, 最高优先级)                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 1. 查询ai_conversation_workflow_context                  │  │
│  │ 2. 判断工作流状态                                         │  │
│  │                                                           │  │
│  │ 场景A: 工作流等待用户输入                                 │  │
│  │   → resumeFlowAsFlux(runtimeUuid, userInput)            │  │
│  │   → 返回 Flux<ChatClientResponse>                        │  │
│  │                                                           │  │
│  │ 场景B: 新对话，执行路由                                   │  │
│  │   → workflowRoutingService.route(userInput)             │  │
│  │   → 如果匹配工作流:                                       │  │
│  │       streaming(workflowUuid) → Flux<ChatClientResponse>│  │
│  │   → 如果未匹配:                                           │  │
│  │       chain.nextStream(request) → 传递给下一个Advisor    │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │ chain.nextStream()
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│  🔹 MessageChatMemoryAdvisor (Order=1)                          │
│     - 管理对话历史（Spring AI标准）                              │
│     - 调用普通AI模型生成回复                                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
                    返回 Flux<ChatClientResponse>
```

### 核心设计原则

1. **符合Spring AI标准**：使用Advisor机制，不侵入ChatClient核心逻辑
2. **单一职责分离**：对话历史 vs 工作流上下文，分表存储
3. **向后兼容**：不修改现有 `ai_conversation` 和 `ai_conversation_content` 表
4. **可扩展性**：支持未来多工作流并发、工作流嵌套等场景

---

## 💾 数据模型设计

### 新增表：ai_conversation_workflow_context

**用途**：存储工作流上下文，独立于对话消息表

```sql
CREATE TABLE ai_conversation_workflow_context (
    id VARCHAR(50) PRIMARY KEY COMMENT '主键ID',
    conversation_id VARCHAR(255) NOT NULL COMMENT '对话ID(FK: ai_conversation.id)',
    workflow_uuid VARCHAR(50) NOT NULL COMMENT '工作流UUID',
    runtime_uuid VARCHAR(50) COMMENT '运行时UUID(用于恢复WorkflowEngine)',
    workflow_state VARCHAR(20) NOT NULL DEFAULT 'IDLE' COMMENT '工作流状态',
    last_interaction_time DATETIME COMMENT '最后交互时间(用于超时清理)',

    c_time DATETIME COMMENT '创建时间',
    c_id BIGINT COMMENT '创建人ID',
    u_time DATETIME COMMENT '更新时间',
    u_id BIGINT COMMENT '更新人ID',
    dbversion INT DEFAULT 0 COMMENT '数据版本(乐观锁)',

    INDEX idx_conversation_id (conversation_id),
    INDEX idx_workflow_state (workflow_state),
    INDEX idx_runtime_uuid (runtime_uuid),
    INDEX idx_last_interaction_time (last_interaction_time)
) COMMENT='AI对话工作流上下文表';
```

### 工作流状态枚举

```java
public class WorkflowStateConstant {
    /** 空闲状态 - 没有活跃工作流 */
    public static final String STATE_IDLE = "IDLE";

    /** 路由中 - 正在进行工作流路由判断 */
    public static final String STATE_ROUTING = "ROUTING";

    /** 执行中 - 工作流正在执行 */
    public static final String STATE_WORKFLOW_RUNNING = "WORKFLOW_RUNNING";

    /** 等待输入 - 工作流暂停,等待用户提供输入 */
    public static final String STATE_WORKFLOW_WAITING_INPUT = "WORKFLOW_WAITING_INPUT";

    /** 已完成 - 工作流执行完成(临时状态,立即转为IDLE) */
    public static final String STATE_WORKFLOW_COMPLETED = "WORKFLOW_COMPLETED";
}
```

### 状态转换流程

```
IDLE
  ↓ (用户输入 → 路由判断)
ROUTING
  ↓ (匹配到工作流)
WORKFLOW_RUNNING
  ↓ (工作流等待用户输入)
WORKFLOW_WAITING_INPUT
  ↓ (用户提供输入 → 恢复执行)
WORKFLOW_RUNNING
  ↓ (工作流执行完成)
WORKFLOW_COMPLETED
  ↓ (立即清理上下文)
IDLE
```

---

## 🔧 核心实现

### 1. 实体类和Mapper

#### AiConversationWorkflowContextEntity.java
```java
package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_conversation_workflow_context")
public class AiConversationWorkflowContextEntity {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("conversation_id")
    private String conversationId;

    @TableField("workflow_uuid")
    private String workflowUuid;

    @TableField("runtime_uuid")
    private String runtimeUuid;

    @TableField("workflow_state")
    private String workflowState;

    @TableField("last_interaction_time")
    private LocalDateTime lastInteractionTime;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
```

#### AiConversationWorkflowContextMapper.java
```java
package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationWorkflowContextEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AiConversationWorkflowContextMapper extends BaseMapper<AiConversationWorkflowContextEntity> {

    /**
     * 查询对话的活跃工作流上下文
     *
     * 只返回处于活跃状态的工作流上下文，包括：
     * - ROUTING: 路由中
     * - WORKFLOW_RUNNING: 执行中
     * - WORKFLOW_WAITING_INPUT: 等待用户输入
     */
    @Select("""
        SELECT id, conversation_id AS conversationId,
               workflow_uuid AS workflowUuid, runtime_uuid AS runtimeUuid,
               workflow_state AS workflowState,
               last_interaction_time AS lastInteractionTime,
               c_time AS cTime, c_id AS cId, u_time AS uTime, u_id AS uId, dbversion
        FROM ai_conversation_workflow_context
        WHERE conversation_id = #{conversationId}
          AND workflow_state IN ('ROUTING', 'WORKFLOW_RUNNING', 'WORKFLOW_WAITING_INPUT')
        ORDER BY last_interaction_time DESC
        LIMIT 1
    """)
    AiConversationWorkflowContextEntity selectActiveByConversationId(@Param("conversationId") String conversationId);

    /**
     * 删除对话的工作流上下文
     */
    @Delete("""
        DELETE FROM ai_conversation_workflow_context
        WHERE conversation_id = #{conversationId}
    """)
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 清理过期的等待输入状态的工作流上下文
     *
     * 用于定时任务清理超时的工作流会话
     */
    @Delete("""
        DELETE FROM ai_conversation_workflow_context
        WHERE last_interaction_time < #{expireTime}
          AND workflow_state = 'WORKFLOW_WAITING_INPUT'
    """)
    int deleteExpiredWaitingContexts(@Param("expireTime") LocalDateTime expireTime);
}
```

### 2. 服务层

#### AiConversationWorkflowContextService.java
```java
package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationWorkflowContextEntity;
import com.xinyirun.scm.ai.common.constant.WorkflowStateConstant;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationWorkflowContextMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AiConversationWorkflowContextService {

    @Resource
    private AiConversationWorkflowContextMapper contextMapper;

    /**
     * 获取对话的活跃工作流上下文
     */
    public AiConversationWorkflowContextEntity getActiveContext(String conversationId) {
        return contextMapper.selectActiveByConversationId(conversationId);
    }

    /**
     * 保存工作流等待用户输入的状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWaitingState(String conversationId, String workflowUuid, String runtimeUuid) {
        AiConversationWorkflowContextEntity context = new AiConversationWorkflowContextEntity();
        context.setConversationId(conversationId);
        context.setWorkflowUuid(workflowUuid);
        context.setRuntimeUuid(runtimeUuid);
        context.setWorkflowState(WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT);
        context.setLastInteractionTime(LocalDateTime.now());

        // 先删除旧的上下文，再插入新的（确保同一对话只有一个活跃工作流）
        contextMapper.deleteByConversationId(conversationId);
        contextMapper.insert(context);

        log.info("保存工作流等待状态: conversationId={}, workflowUuid={}, runtimeUuid={}",
            conversationId, workflowUuid, runtimeUuid);
    }

    /**
     * 删除对话的工作流上下文
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteContext(String conversationId) {
        int deleted = contextMapper.deleteByConversationId(conversationId);
        if (deleted > 0) {
            log.info("删除工作流上下文: conversationId={}", conversationId);
        }
    }

    /**
     * 定时清理过期的工作流上下文
     *
     * 每10分钟执行一次，清理超过10分钟未交互的等待输入状态的工作流
     * 与InterruptedFlow.RUNTIME_TO_GRAPH的10分钟TTL保持一致
     */
    @Scheduled(fixedRate = 600000)
    public void cleanupExpiredContexts() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(10);
        int deleted = contextMapper.deleteExpiredWaitingContexts(expireTime);
        if (deleted > 0) {
            log.info("清理过期工作流上下文: {} 条", deleted);
        }
    }
}
```

### 3. WorkflowStarter扩展

#### 新增方法：resumeFlowAsFlux()
```java
/**
 * 恢复工作流执行（流式版本）
 *
 * 与resumeFlow()的区别：
 * - resumeFlow(): 同步void方法，用于同步场景
 * - resumeFlowAsFlux(): 返回Flux<WorkflowEventVo>，用于流式响应
 *
 * @param runtimeUuid 运行时UUID
 * @param userInput 用户输入
 * @return 工作流事件流
 */
public Flux<WorkflowEventVo> resumeFlowAsFlux(String runtimeUuid, String userInput) {
    String executionId = UUID.randomUUID().toString();

    return Flux.<WorkflowEventVo>create(fluxSink -> {
        try {
            // 1. 从缓存中获取WorkflowEngine
            WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);
            if (workflowEngine == null) {
                fluxSink.error(new RuntimeException("工作流会话已过期，请重新发起"));
                return;
            }

            // 2. 创建新的StreamHandler
            WorkflowStreamHandler streamHandler = new WorkflowStreamHandler(
                new WorkflowStreamHandler.StreamCallback() {
                    @Override
                    public void onStart(String runtimeData) {
                        fluxSink.next(WorkflowEventVo.createStartEvent(runtimeData));
                    }

                    @Override
                    public void onNodeRun(String nodeUuid, String nodeData) {
                        fluxSink.next(WorkflowEventVo.createNodeRunEvent(nodeUuid, nodeData));
                    }

                    @Override
                    public void onNodeInput(String nodeUuid, String inputData) {
                        fluxSink.next(WorkflowEventVo.createNodeInputEvent(nodeUuid, inputData));
                    }

                    @Override
                    public void onNodeOutput(String nodeUuid, String outputData) {
                        fluxSink.next(WorkflowEventVo.createNodeOutputEvent(nodeUuid, outputData));
                    }

                    @Override
                    public void onNodeChunk(String nodeUuid, String chunk) {
                        fluxSink.next(WorkflowEventVo.createNodeChunkEvent(nodeUuid, chunk));
                    }

                    @Override
                    public void onNodeWaitFeedback(String nodeUuid, String tip) {
                        fluxSink.next(WorkflowEventVo.createNodeWaitFeedbackEvent(nodeUuid, tip));
                    }

                    @Override
                    public void onComplete(String data) {
                        fluxSink.next(WorkflowEventVo.createDoneEvent(data));
                        fluxSink.complete();
                    }

                    @Override
                    public void onError(Throwable error) {
                        fluxSink.error(error);
                    }
                }
            );

            // 3. 替换WorkflowEngine的streamHandler
            workflowEngine.setStreamHandler(streamHandler);

            // 4. 异步恢复执行
            self.asyncResumeWorkflow(workflowEngine, userInput);

        } catch (Exception e) {
            log.error("恢复工作流失败: runtimeUuid={}, error={}", runtimeUuid, e.getMessage());
            fluxSink.error(e);
        }
    })
    .subscribeOn(Schedulers.boundedElastic())
    .doFinally(signalType -> {
        DataSourceHelper.close();
    });
}

/**
 * 异步恢复工作流执行
 */
@Async("workflowExecutor")
public void asyncResumeWorkflow(WorkflowEngine workflowEngine, String userInput) {
    try {
        workflowEngine.resume(userInput);
    } catch (Exception e) {
        log.error("异步恢复工作流失败", e);
        throw e;
    }
}
```

### 4. WorkflowContextAdvisor实现

```java
package com.xinyirun.scm.ai.advisor;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationWorkflowContextEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.common.constant.WorkflowStateConstant;
import com.xinyirun.scm.ai.core.service.chat.AiConversationWorkflowContextService;
import com.xinyirun.scm.ai.core.service.workflow.WorkflowRoutingService;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.StreamAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流上下文管理Advisor
 *
 * 职责：
 * 1. 判断是否需要继续执行工作流
 * 2. 执行工作流路由判断
 * 3. 管理工作流上下文生命周期
 * 4. 将WorkflowEventVo转换为ChatClientResponse
 *
 * 优先级：最高(Order=0)，在MessageChatMemoryAdvisor之前执行
 */
@Slf4j
@Component
public class WorkflowContextAdvisor implements StreamAdvisor {

    @Resource
    private AiConversationWorkflowContextService workflowContextService;

    @Resource
    private WorkflowStarter workflowStarter;

    @Resource
    private WorkflowRoutingService workflowRoutingService;

    @Override
    public String getName() {
        return "WorkflowContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0; // 最高优先级
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        String conversationId = (String) request.advisorParams().get(ChatMemory.CONVERSATION_ID);
        String userInput = request.userText();
        Long userId = SecurityUtil.getStaff_id();
        String tenantCode = extractTenantCode(conversationId);

        // 查询活跃的工作流上下文
        return Mono.fromCallable(() -> workflowContextService.getActiveContext(conversationId))
            .flatMapMany(context -> {
                if (context != null && isWaitingForInput(context)) {
                    // 场景A: 工作流等待用户输入，恢复执行
                    log.info("恢复工作流: conversationId={}, runtimeUuid={}",
                        conversationId, context.getRuntimeUuid());
                    return resumeWorkflow(conversationId, context.getRuntimeUuid(), userInput);

                } else {
                    // 场景B: 新对话或工作流已完成，执行路由
                    return routeAndExecute(conversationId, userInput, userId, tenantCode, chain, request);
                }
            });
    }

    /**
     * 判断工作流是否在等待用户输入
     */
    private boolean isWaitingForInput(AiConversationWorkflowContextEntity context) {
        return WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT.equals(context.getWorkflowState());
    }

    /**
     * 场景A: 恢复工作流执行
     */
    private Flux<ChatClientResponse> resumeWorkflow(String conversationId, String runtimeUuid, String userInput) {
        return workflowStarter.resumeFlowAsFlux(runtimeUuid, userInput)
            .doOnNext(event -> handleWorkflowEvent(conversationId, null, event))
            .map(this::convertWorkflowEventToChatClientResponse)
            .doOnError(error -> {
                log.error("恢复工作流失败: conversationId={}, error={}", conversationId, error.getMessage());
                workflowContextService.deleteContext(conversationId);
            })
            .doOnComplete(() -> {
                log.info("工作流恢复执行完成: conversationId={}", conversationId);
                workflowContextService.deleteContext(conversationId);
            });
    }

    /**
     * 场景B: 路由并执行工作流
     */
    private Flux<ChatClientResponse> routeAndExecute(
            String conversationId, String userInput, Long userId,
            String tenantCode, StreamAdvisorChain chain, ChatClientRequest request) {

        // 执行路由判断
        String workflowUuid = workflowRoutingService.route(userInput, userId, null);

        if (workflowUuid != null) {
            // 匹配到工作流，执行
            log.info("路由到工作流: conversationId={}, workflowUuid={}", conversationId, workflowUuid);
            return executeWorkflow(conversationId, workflowUuid, userInput, tenantCode);

        } else {
            // 未匹配到工作流，传递给下一个Advisor（普通AI对话）
            log.debug("未匹配工作流，走普通对话: conversationId={}", conversationId);
            return chain.nextStream(request);
        }
    }

    /**
     * 执行工作流并监听事件
     */
    private Flux<ChatClientResponse> executeWorkflow(
            String conversationId, String workflowUuid, String userInput, String tenantCode) {

        return workflowStarter.streaming(workflowUuid, new ArrayList<>(), tenantCode)
            .doOnNext(event -> handleWorkflowEvent(conversationId, workflowUuid, event))
            .map(this::convertWorkflowEventToChatClientResponse)
            .doOnError(error -> {
                log.error("工作流执行失败: conversationId={}, error={}", conversationId, error.getMessage());
                workflowContextService.deleteContext(conversationId);
            })
            .doOnComplete(() -> {
                log.info("工作流执行完成: conversationId={}", conversationId);
                workflowContextService.deleteContext(conversationId);
            });
    }

    /**
     * 处理工作流事件（副作用）
     */
    private void handleWorkflowEvent(String conversationId, String workflowUuid, WorkflowEventVo event) {
        if ("NODE_WAIT_FEEDBACK".equals(event.getType())) {
            // 工作流等待用户输入，保存上下文
            String runtimeUuid = event.getData(); // runtimeUuid存储在data字段
            log.info("工作流等待用户输入: conversationId={}, runtimeUuid={}", conversationId, runtimeUuid);
            workflowContextService.saveWaitingState(conversationId, workflowUuid, runtimeUuid);
        }
    }

    /**
     * 将WorkflowEventVo转换为ChatClientResponse
     *
     * 这是关键的适配层，将工作流事件转换为Spring AI标准的响应格式
     */
    private ChatClientResponse convertWorkflowEventToChatClientResponse(WorkflowEventVo event) {
        // 根据事件类型构造不同的响应
        String content;
        switch (event.getType()) {
            case "NODE_CHUNK":
                content = event.getData(); // 流式内容块
                break;
            case "NODE_WAIT_FEEDBACK":
                content = event.getMessage(); // 等待用户输入的提示
                break;
            case "WORKFLOW_DONE":
                content = event.getData(); // 工作流完成的输出
                break;
            default:
                content = ""; // 其他事件类型不返回内容
        }

        // 构造ChatResponse
        org.springframework.ai.chat.model.ChatResponse chatResponse =
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new org.springframework.ai.chat.model.Generation(
                    new org.springframework.ai.chat.messages.AssistantMessage(content)
                ))
            );

        // 包装为ChatClientResponse（需要executionContext，可为空Map）
        return new ChatClientResponse(chatResponse, Map.of());
    }

    /**
     * 从conversationId提取tenantCode
     *
     * conversationId格式: tenant_code::conversation_uuid
     */
    private String extractTenantCode(String conversationId) {
        if (conversationId != null && conversationId.contains("::")) {
            return conversationId.split("::", 2)[0];
        }
        return null;
    }
}
```

### 5. ChatClient配置

```java
package com.xinyirun.scm.ai.config;

import com.xinyirun.scm.ai.advisor.WorkflowContextAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI ChatClient配置
 *
 * 集成Spring AI的ChatClient和自定义Advisor
 */
@Configuration
public class AiChatClientConfig {

    /**
     * ChatMemory配置
     *
     * 使用MessageWindowChatMemory，保留最近10条消息
     * 未来可以切换为JdbcChatMemory或RedisChatMemory
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();
    }

    /**
     * ChatClient配置
     *
     * Advisor执行顺序：
     * 1. WorkflowContextAdvisor (Order=0) - 工作流路由和恢复
     * 2. MessageChatMemoryAdvisor (Order=1) - 对话历史管理
     */
    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            ChatMemory chatMemory,
            WorkflowContextAdvisor workflowContextAdvisor) {

        return ChatClient.builder(chatModel)
            .defaultAdvisors(
                workflowContextAdvisor, // 工作流上下文管理（最高优先级）
                MessageChatMemoryAdvisor.builder(chatMemory).build() // 对话历史管理
            )
            .build();
    }
}
```

### 6. AiConversationController集成

```java
/**
 * AI流式聊天 - 集成工作流路由
 */
@PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@Operation(summary = "流式聊天 (Spring AI标准)")
@SysLogAnnotion("AI流式聊天")
public Flux<ChatResponseVo> chatStream(@Validated @RequestBody AIChatRequestVo request) {
    Long operatorId = SecurityUtil.getStaff_id();
    String userId = operatorId.toString();
    String conversationId = request.getConversationId();
    String tenantCode = extractTenantCode(conversationId);

    // 在后台线程异步处理
    return Flux.<ChatResponseVo>create(fluxSink -> {
        try {
            DataSourceHelper.use(tenantCode);

            // 1. 保存用户消息到ai_conversation_content
            aiConversationContentService.saveConversationContent(
                conversationId,
                AiMessageTypeConstant.MESSAGE_TYPE_USER,
                request.getPrompt(),
                null, null, null,
                operatorId
            );

            // 2. 调用ChatClient（自动触发Advisor链）
            Flux<org.springframework.ai.chat.client.ChatClientResponse> chatClientFlux =
                chatClient.prompt()
                    .user(request.getPrompt())
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .chatClientResponse();

            // 3. 转换ChatClientResponse → ChatResponseVo
            chatClientFlux
                .map(chatClientResponse -> {
                    String content = chatClientResponse.chatResponse()
                        .getResult()
                        .getOutput()
                        .getContent();
                    return ChatResponseVo.createContentChunk(content);
                })
                .doOnComplete(() -> {
                    // 保存AI回复消息到ai_conversation_content
                    // （由MessageChatMemoryAdvisor自动管理，这里可选）
                    fluxSink.complete();
                })
                .doOnError(error -> {
                    log.error("AI对话失败: conversationId={}, error={}", conversationId, error.getMessage());
                    fluxSink.error(error);
                })
                .subscribe(
                    chatResponseVo -> fluxSink.next(chatResponseVo),
                    error -> fluxSink.error(error),
                    () -> fluxSink.complete()
                );

        } catch (Exception e) {
            log.error("AI对话异常", e);
            fluxSink.error(e);
        }
    })
    .subscribeOn(Schedulers.boundedElastic())
    .doFinally(signalType -> {
        DataSourceHelper.close();
    });
}

private String extractTenantCode(String conversationId) {
    if (conversationId != null && conversationId.contains("::")) {
        return conversationId.split("::", 2)[0];
    }
    return null;
}
```

---

## 📊 完整流程图

### 场景1: 首次工作流调用

```
用户: "帮我查询订单"
  ↓
AiConversationController.chatStream()
  ↓ 保存USER消息到ai_conversation_content
  ↓
ChatClient.prompt()
  .user("帮我查询订单")
  .advisors(a -> a.param(CONVERSATION_ID, "tenant::conv123"))
  .stream().chatClientResponse()
  ↓
WorkflowContextAdvisor.adviseStream()
  ↓ 查询workflow_context (NULL)
  ↓
WorkflowRoutingService.route("帮我查询订单", userId, null)
  ↓ 返回: "order_query_workflow_uuid"
  ↓
WorkflowStarter.streaming("order_query_workflow_uuid", [], tenantCode)
  ↓ 工作流执行...
  ↓ 到达参数收集节点
  ↓
streamHandler.onNodeWaitFeedback("请提供订单号")
  ↓ fluxSink.next(WorkflowEventVo.createNodeWaitFeedbackEvent(...))
  ↓
WorkflowContextAdvisor.handleWorkflowEvent()
  ↓ 保存workflow_context:
      - conversation_id: "tenant::conv123"
      - workflow_uuid: "order_query_workflow_uuid"
      - runtime_uuid: "runtime_abc123"
      - workflow_state: "WORKFLOW_WAITING_INPUT"
  ↓
convertWorkflowEventToChatClientResponse()
  ↓ 转换为ChatClientResponse
  ↓
返回给前端: "请提供订单号"
```

### 场景2: 继续工作流

```
用户: "ORD-20251110-001"
  ↓
AiConversationController.chatStream()
  ↓ 保存USER消息到ai_conversation_content
  ↓
ChatClient.prompt()
  .user("ORD-20251110-001")
  .advisors(a -> a.param(CONVERSATION_ID, "tenant::conv123"))
  .stream().chatClientResponse()
  ↓
WorkflowContextAdvisor.adviseStream()
  ↓ 查询workflow_context:
      - workflow_state: "WORKFLOW_WAITING_INPUT"
      - runtime_uuid: "runtime_abc123"
  ↓ 判断: isWaitingForInput() = true
  ↓
WorkflowStarter.resumeFlowAsFlux("runtime_abc123", "ORD-20251110-001")
  ↓ 从InterruptedFlow.RUNTIME_TO_GRAPH获取WorkflowEngine
  ↓ 替换streamHandler
  ↓ 调用asyncResumeWorkflow()
  ↓
WorkflowEngine.resume("ORD-20251110-001")
  ↓ 工作流继续执行...
  ↓ 执行订单查询逻辑
  ↓ 完成执行
  ↓
streamHandler.onComplete("订单查询结果: ...")
  ↓ fluxSink.next(WorkflowEventVo.createDoneEvent(...))
  ↓ fluxSink.complete()
  ↓
WorkflowContextAdvisor.doOnComplete()
  ↓ 删除workflow_context
  ↓
convertWorkflowEventToChatClientResponse()
  ↓ 转换为ChatClientResponse
  ↓
返回给前端: "订单查询结果: ..."
```

### 场景3: 未匹配工作流，走普通对话

```
用户: "今天天气怎么样？"
  ↓
AiConversationController.chatStream()
  ↓
WorkflowContextAdvisor.adviseStream()
  ↓ 查询workflow_context (NULL)
  ↓
WorkflowRoutingService.route("今天天气怎么样？", userId, null)
  ↓ 返回: null (未匹配到工作流)
  ↓
chain.nextStream(request)
  ↓ 传递给MessageChatMemoryAdvisor
  ↓
MessageChatMemoryAdvisor.adviseStream()
  ↓ 加载对话历史
  ↓ 调用普通AI模型
  ↓ 返回Flux<ChatClientResponse>
  ↓
返回给前端: "今天天气晴朗，温度适宜..."
```

---

## 🎯 核心优势

### 1. 符合Spring AI标准
- ✅ 使用Advisor机制，不侵入ChatClient核心
- ✅ 与MessageChatMemoryAdvisor无缝配合
- ✅ 支持Reactive流式响应（Flux）

### 2. 架构清晰
- ✅ 对话历史 vs 工作流上下文，职责分离
- ✅ 独立的workflow_context表，易于扩展
- ✅ 工作流状态管理与对话消息解耦

### 3. 可扩展性
- ✅ 支持未来多工作流并发（一个对话同时运行多个工作流）
- ✅ 支持工作流嵌套（子工作流调用）
- ✅ 支持不同类型的ChatMemory后端（JDBC、Redis、MongoDB）

### 4. 向后兼容
- ✅ 不修改现有 `ai_conversation` 和 `ai_conversation_content` 表
- ✅ 现有对话功能不受影响
- ✅ 渐进式集成，平滑升级

---

## ⚠️ 风险评估

### 技术风险

| 风险项 | 风险等级 | 缓解措施 |
|--------|---------|---------|
| WorkflowEngine.setStreamHandler()方法不存在 | 🟡 中 | 需要为WorkflowEngine添加此方法，或使用反射/构造函数注入 |
| Flux流式响应的异常处理 | 🟡 中 | 完善doOnError、doOnComplete、doFinally逻辑 |
| 并发场景下的上下文竞争 | 🟡 中 | 使用事务+乐观锁，确保同一对话只有一个活跃工作流 |
| 工作流超时未清理 | 🟢 低 | 定时任务清理过期上下文，与InterruptedFlow.RUNTIME_TO_GRAPH的10分钟TTL保持一致 |

### 性能风险

| 风险项 | 风险等级 | 缓解措施 |
|--------|---------|---------|
| workflow_context表查询频率高 | 🟢 低 | 已添加索引 `idx_conversation_id`，查询性能良好 |
| Flux流式响应内存占用 | 🟢 低 | 使用backpressure机制，避免内存溢出 |

---

## 📝 实施计划

### 阶段1: 数据模型准备（1天）
- [ ] 创建 `ai_conversation_workflow_context` 表
- [ ] 编写 `AiConversationWorkflowContextEntity`
- [ ] 编写 `AiConversationWorkflowContextMapper`
- [ ] 编写 `AiConversationWorkflowContextService`

### 阶段2: WorkflowStarter扩展（1天）
- [ ] 为 `WorkflowEngine` 添加 `setStreamHandler()` 方法
- [ ] 实现 `WorkflowStarter.resumeFlowAsFlux()`
- [ ] 实现 `WorkflowStarter.asyncResumeWorkflow()`
- [ ] 单元测试验证

### 阶段3: Advisor实现（1天）
- [ ] 实现 `WorkflowContextAdvisor`
- [ ] 实现 `convertWorkflowEventToChatClientResponse()`
- [ ] 实现 `handleWorkflowEvent()`
- [ ] 集成测试验证

### 阶段4: ChatClient配置（0.5天）
- [ ] 编写 `AiChatClientConfig`
- [ ] 配置 `ChatMemory` 和 Advisor链
- [ ] 验证Advisor执行顺序

### 阶段5: Controller集成（0.5天）
- [ ] 修改 `AiConversationController.chatStream()`
- [ ] 集成 `ChatClient`
- [ ] 适配 `ChatClientResponse` → `ChatResponseVo`

### 阶段6: 测试验收（1天）
- [ ] 场景1：首次工作流调用测试
- [ ] 场景2：多轮对话恢复测试
- [ ] 场景3：未匹配工作流，走普通对话测试
- [ ] 场景4：工作流超时清理测试
- [ ] 性能测试和压力测试

**总计**: 5天

---

## ✅ 验收标准

### 功能验收
1. ✅ 用户输入匹配工作流时，能正确路由并执行工作流
2. ✅ 工作流等待用户输入时，能保存上下文并暂停
3. ✅ 用户提供输入后，能恢复工作流执行并返回结果
4. ✅ 用户输入未匹配工作流时，能走普通AI对话
5. ✅ 工作流超时（10分钟）后，能自动清理上下文

### 性能验收
1. ✅ workflow_context表查询响应时间 < 10ms
2. ✅ 工作流路由判断时间 < 100ms
3. ✅ 流式响应首字节时间 < 500ms

### 兼容性验收
1. ✅ 现有对话功能不受影响
2. ✅ 现有数据表结构不变
3. ✅ 前端不需要修改（SSE流式响应保持一致）

---

## 📚 参考资料

- [Spring AI官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI Advisor机制](https://docs.spring.io/spring-ai/reference/api/advisors.html)
- [Spring AI Chat Memory](https://docs.spring.io/spring-ai/reference/api/chat-memory.html)
- [LangGraph4j文档](https://github.com/bsorrentino/langgraph4j)
- [Project Reactor文档](https://projectreactor.io/docs/core/release/reference/)

---

## 📞 联系方式

如有疑问或需要进一步澄清，请联系：
- **技术负责人**: SCM-AI团队
- **邮箱**: scm-ai@xinyirun.com
