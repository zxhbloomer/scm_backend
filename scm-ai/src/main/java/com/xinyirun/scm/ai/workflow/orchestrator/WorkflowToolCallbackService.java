package com.xinyirun.scm.ai.workflow.orchestrator;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowService;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Workflow动态包装服务
 *
 * 设计理念:
 * - 不使用缓存,每次都查询数据库获取最新workflow配置
 * - 用户在前端页面创建workflow后,立即可在AI Chat中使用
 * - 无需重启应用,真正的动态发现和包装
 *
 * @author zzxxhh
 * @since 2025-11-25
 */
@Service
@Slf4j
public class WorkflowToolCallbackService {

    @Autowired
    private AiWorkflowService workflowService;

    @Autowired
    private WorkflowStarter workflowStarter;

    /**
     * 获取单个workflow的ToolCallback(每次都查数据库)
     *
     * @param workflowUuid workflow唯一标识
     * @return ToolCallback对象,如果workflow不存在或未启用则返回null
     */
    public ToolCallback getCallback(String workflowUuid) {
        // 查询数据库获取最新workflow配置
        AiWorkflowEntity workflow = workflowService.lambdaQuery()
                .eq(AiWorkflowEntity::getWorkflowUuid, workflowUuid)
                .eq(AiWorkflowEntity::getIsDeleted, 0)
                .eq(AiWorkflowEntity::getIsEnable, 1)
                .one();

        if (workflow == null) {
            log.warn("【WorkflowToolCallbackService】Workflow不存在或未启用: workflowUuid={}", workflowUuid);
            return null;
        }

        // 使用静态工厂方法创建ToolCallback对象
        return WorkflowToolCallback.create(
                workflow.getWorkflowUuid(),
                workflow.getTitle(),
                workflow.getRemark(),  // 使用remark字段作为描述
                null,  // inputConfig暂时传null,后续可以从数据库字段获取
                workflowStarter
        );
    }

    /**
     * 获取所有启用的workflow列表(给Orchestrator的prompt用)
     *
     * 用途: Orchestrator需要知道当前系统中有哪些可用的workflow,
     *      才能正确分解任务并指定target
     *
     * @return ToolCallback列表
     */
    public List<ToolCallback> getAllCallbacks() {
        // 查询所有启用的workflow
        List<AiWorkflowEntity> workflows = workflowService.lambdaQuery()
                .eq(AiWorkflowEntity::getIsDeleted, 0)
                .eq(AiWorkflowEntity::getIsEnable, 1)
                .list();

        log.info("【WorkflowToolCallbackService】查询到{}个启用的workflow", workflows.size());

        // 转换为ToolCallback列表
        return workflows.stream()
                .map(w -> WorkflowToolCallback.create(
                        w.getWorkflowUuid(),
                        w.getTitle(),
                        w.getRemark(),  // 使用remark字段作为描述
                        null,  // inputConfig暂时传null,后续可以从数据库字段获取
                        workflowStarter
                ))
                .collect(Collectors.toList());
    }
}
