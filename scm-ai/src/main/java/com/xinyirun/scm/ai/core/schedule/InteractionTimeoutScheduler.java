package com.xinyirun.scm.ai.core.schedule;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowInteractionEntity;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowInteractionService;
import com.xinyirun.scm.ai.workflow.InterruptedFlow;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * 人机交互超时定时任务
 *
 * <p>每60秒扫描一次已超时的WAITING状态交互记录，
 * 将其状态更新为TIMEOUT，并终止对应的工作流</p>
 *
 * @author SCM-AI团队
 * @since 2026-03-06
 */
@Slf4j
@Component
public class InteractionTimeoutScheduler {

    @Resource
    private AiWorkflowInteractionService interactionService;

    /**
     * 每60秒扫描超时交互记录
     * 遍历所有租户数据源执行
     */
    @Scheduled(fixedDelay = 60000)
    public void scanExpiredInteractions() {
        Map<String, DataSource> dataSources = DataSourceHelper.getDataSource();
        for (String dsName : dataSources.keySet()) {
            // 跳过master主数据源
            if ("master".equals(dsName)) {
                continue;
            }
            try {
                DataSourceHelper.use(dsName);
                processExpiredForCurrentDs(dsName);
            } catch (Exception e) {
                log.error("扫描租户{}超时交互异常", dsName, e);
            } finally {
                DataSourceHelper.close();
            }
        }
    }

    private void processExpiredForCurrentDs(String dsName) {
        List<AiWorkflowInteractionEntity> expired = interactionService.findExpiredInteractions();
        if (expired.isEmpty()) {
            return;
        }

        log.info("租户{}发现{}条超时交互记录", dsName, expired.size());
        for (AiWorkflowInteractionEntity entity : expired) {
            try {
                interactionService.timeoutInteraction(entity.getInteractionUuid());

                // 终止对应的工作流（从内存缓存中移除，切断恢复路径）
                String runtimeUuid = entity.getRuntimeUuid();
                if (runtimeUuid != null) {
                    InterruptedFlow.RUNTIME_TO_GRAPH.remove(runtimeUuid);
                    log.info("超时终止工作流: interactionUuid={}, runtimeUuid={}",
                        entity.getInteractionUuid(), runtimeUuid);
                }
            } catch (Exception e) {
                log.error("处理超时交互失败: interactionUuid={}", entity.getInteractionUuid(), e);
            }
        }
    }
}
