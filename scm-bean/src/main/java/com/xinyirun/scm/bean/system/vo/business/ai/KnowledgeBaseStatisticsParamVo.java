package com.xinyirun.scm.bean.system.vo.business.ai;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI知识库统计任务参数VO
 *
 * <p>该类用于Quartz任务的参数传递（通过JSON序列化）</p>
 * <p>对应SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.PARAM_CLASS</p>
 *
 * @author SCM AI Team
 * @since 2025-10-11
 */
@Data
public class KnowledgeBaseStatisticsParamVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 1923948879272832572L;

    /**
     * 知识库UUID（格式：{tenantCode}::{uuid}）
     * 必填
     */
    private String kbUuid;

    /**
     * 租户code
     * 必填，虽然AbstractQuartzJob会自动切换数据源，但保留此字段便于日志和校验
     */
    private String tenantCode;

    /**
     * 触发原因（可选，用于日志追踪）
     * 如："文档索引完成"、"文档删除"等
     */
    private String triggerReason;
}
