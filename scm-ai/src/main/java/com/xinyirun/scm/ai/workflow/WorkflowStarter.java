package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * 工作流启动器
 *
 * <p>负责工作流的流式执行和中断恢复</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Component
public class WorkflowStarter {

    @Resource
    private AiWorkflowService workflowService;

    @Resource
    private AiWorkflowNodeService workflowNodeService;

    @Resource
    private AiWorkflowEdgeService workflowEdgeService;

    @Resource
    private AiWorkflowComponentService workflowComponentService;

    @Resource
    private AiWorkflowRuntimeService workflowRuntimeService;

    @Resource
    private AiWorkflowRuntimeNodeService workflowRuntimeNodeService;

    /**
     * 流式执行工作流
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码（从Controller层传递，用于异步线程数据源切换）
     * @return Flux流式响应
     */
    public Flux<WorkflowEventVo> streaming(String workflowUuid, List<JSONObject> userInputs, String tenantCode) {
        Long userId = SecurityUtil.getStaff_id();

        return Flux.<WorkflowEventVo>create(fluxSink -> {
            try {
                // 【多租户关键】在异步线程中切换到正确的数据源
                DataSourceHelper.use(tenantCode);

                // 获取工作流配置
                AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

                // 检查工作流是否启用
                if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
                    fluxSink.error(new BusinessException("工作流已禁用"));
                    return;
                }

                log.info("WorkflowEngine run,userId:{},workflowUuid:{},tenantCode:{},userInputs:{}",
                        userId, workflow.getWorkflowUuid(), tenantCode, userInputs);

                // 获取工作流组件、节点、边配置
                List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
                List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
                List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

                // 创建工作流流式回调处理器
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

                // 创建工作流引擎并执行
                WorkflowEngine workflowEngine = new WorkflowEngine(
                        workflow,
                        streamHandler,
                        components,
                        nodes,
                        edges,
                        workflowRuntimeService,
                        workflowRuntimeNodeService
                );
                workflowEngine.run(userId, userInputs);

            } catch (Exception e) {
                log.error("工作流执行异常: workflowUuid={}, userId={}", workflowUuid, userId, e);
                fluxSink.error(e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic()) // 在弹性线程池中执行
        .doFinally(signalType -> {
            // 清理数据源上下文
            DataSourceHelper.close();
        });
    }

    /**
     * 恢复中断的工作流
     *
     * @param runtimeUuid 运行实例UUID
     * @param userInput 用户输入
     */
    @Async
    public void resumeFlow(String runtimeUuid, String userInput) {
        // 参考 aideepin: WorkflowStarter.resumeFlow() 第90-97行
        WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);
        if (workflowEngine == null) {
            log.error("工作流恢复执行时失败,runtime:{}", runtimeUuid);
            throw new RuntimeException("工作流实例不存在或已超时");
        }

        // 调用engine的resume方法恢复工作流
        // 参考 aideepin: WorkflowStarter.resumeFlow() 第96行
        workflowEngine.resume(userInput);
    }

}
