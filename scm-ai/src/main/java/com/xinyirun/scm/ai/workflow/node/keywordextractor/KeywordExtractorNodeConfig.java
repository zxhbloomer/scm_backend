package com.xinyirun.scm.ai.workflow.node.keywordextractor;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 工作流关键词提取节点配置
 *
 * @author zxh
 * @since 2025-10-27
 */
@Data
public class KeywordExtractorNodeConfig {

    /**
     * 提取前N个关键词
     */
    @JSONField(name = "top_n")
    private Integer topN;

    /**
     * 使用的模型名称
     */
    @JSONField(name = "model_name")
    private String modelName;
}
