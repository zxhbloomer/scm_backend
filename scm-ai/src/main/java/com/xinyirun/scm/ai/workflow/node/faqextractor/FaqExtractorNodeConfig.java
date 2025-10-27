package com.xinyirun.scm.ai.workflow.node.faqextractor;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 工作流FAQ提取节点配置
 * 参考 aideepin: com.moyz.adi.common.workflow.node.faqextractor.FaqExtractorNodeConfig
 *
 * 转换说明：
 * - @JsonProperty → @JSONField (Fastjson2)
 * - 移除 @EqualsAndHashCode（非必需）
 *
 * @author SCM AI Team
 * @since 2025-10-27
 */
@Data
public class FaqExtractorNodeConfig {

    /**
     * 提取前N组FAQ
     */
    @JSONField(name = "top_n")
    private Integer topN;

    /**
     * 使用的模型名称
     */
    @JSONField(name = "model_name")
    private String modelName;
}
