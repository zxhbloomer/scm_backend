package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.bean.system.vo.business.ai.KnowledgeBaseStatisticsParamVo;
import com.xinyirun.scm.ai.core.service.elasticsearch.ElasticsearchIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI知识库统计服务
 *
 * <p>被Quartz反射调用的目标服务类</p>
 * <p>对应aideepin: KnowledgeBaseService.asyncUpdateStatistic()</p>
 *
 * <h3>调用链：</h3>
 * <ol>
 *   <li>DocumentIndexingService.finally{} - 文档索引完成后触发</li>
 *   <li>ScheduleUtils.createJobKnowledgeBaseStatistics() - 创建Quartz任务</li>
 *   <li>AbstractQuartzJob.execute() - 自动切换到租户数据源（三重上下文保障）</li>
 *   <li>通过反射调用本方法：updateStatistics(KnowledgeBaseStatisticsParamVo)</li>
 *   <li>统计embedding数量并更新MySQL</li>
 *   <li>AbstractQuartzJob.after() - 自动记录日志到双库</li>
 * </ol>
 *
 * <h3>与aideepin的对比：</h3>
 * <table>
 *   <tr>
 *     <th>维度</th>
 *     <th>aideepin</th>
 *     <th>scm-ai（本类）</th>
 *   </tr>
 *   <tr>
 *     <td>调度方式</td>
 *     <td>@Scheduled(fixedDelay=60000)</td>
 *     <td>Quartz SimpleTrigger（单次执行）</td>
 *   </tr>
 *   <tr>
 *     <td>触发机制</td>
 *     <td>定时轮询Redis Set</td>
 *     <td>文档索引完成后直接创建任务</td>
 *   </tr>
 *   <tr>
 *     <td>租户隔离</td>
 *     <td>单租户架构</td>
 *     <td>AbstractQuartzJob三重上下文保障</td>
 *   </tr>
 *   <tr>
 *     <td>执行日志</td>
 *     <td>无</td>
 *     <td>自动记录到s_job_log + s_job_log_manager</td>
 *   </tr>
 * </table>
 *
 * @author SCM AI Team
 * @since 2025-10-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseStatisticsService {

    private final AiKnowledgeBaseMapper knowledgeBaseMapper;
    private final ElasticsearchIndexingService elasticsearchIndexingService;

    /**
     * 更新指定知识库的统计数据
     *
     * <p>⚠️ 方法签名必须严格匹配：</p>
     * <ul>
     *   <li>访问修饰符：public</li>
     *   <li>返回类型：void</li>
     *   <li>方法名：updateStatistics（对应SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.METHOD_NAME）</li>
     *   <li>参数类型：KnowledgeBaseStatisticsParamVo（对应SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.PARAM_CLASS）</li>
     * </ul>
     *
     * <p>参考aideepin代码：</p>
     * <pre>
     * {@literal @Scheduled}(fixedDelay = 60 * 1000)
     * public void asyncUpdateStatistic() {
     *     Set<String> kbUuidList = stringRedisTemplate.opsForSet().members(KB_STATISTIC_RECALCULATE_SIGNAL);
     *     if (CollectionUtils.isEmpty(kbUuidList)) {
     *         return;
     *     }
     *     for (String kbUuid : kbUuidList) {
     *         int embeddingCount = embeddingService.countByKbUuid(kbUuid);
     *         baseMapper.updateStatByUuid(kbUuid, embeddingCount);
     *         stringRedisTemplate.opsForSet().remove(KB_STATISTIC_RECALCULATE_SIGNAL, kbUuid);
     *     }
     * }
     * </pre>
     *
     * @param param 参数对象（包含kbUuid、tenantCode、triggerReason）
     */
    public void updateStatistics(KnowledgeBaseStatisticsParamVo param) {
        String kbUuid = param.getKbUuid();
        String tenantCode = param.getTenantCode();
        String triggerReason = param.getTriggerReason();

        log.info("开始执行AI知识库统计任务，kbUuid: {}, tenant: {}, trigger: {}",
                kbUuid, tenantCode, triggerReason);

        try {
            // 统计向量数量（Elasticsearch）
            // 注意：此时AbstractQuartzJob已自动切换到租户数据源
            long embeddingCount = elasticsearchIndexingService.countEmbeddingsByKbUuid(kbUuid);

            // 更新知识库统计（MySQL - 租户库）
            // 对应aideepin: baseMapper.updateStatByUuid(kbUuid, embeddingCount)
            knowledgeBaseMapper.updateStatByUuid(kbUuid, (int) embeddingCount);

            log.info("知识库统计更新成功，kbUuid: {}, embeddingCount: {}", kbUuid, embeddingCount);

        } catch (Exception e) {
            log.error("知识库统计更新失败，kbUuid: {}, error: {}", kbUuid, e.getMessage(), e);
            // 注意：抛出异常会被AbstractQuartzJob捕获并记录到日志表
            throw new RuntimeException("统计更新失败: " + e.getMessage(), e);
        }
    }
}
