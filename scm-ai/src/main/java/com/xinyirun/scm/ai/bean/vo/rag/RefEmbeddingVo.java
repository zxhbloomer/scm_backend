package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 向量引用VO
 *
 * 
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefEmbeddingVo {

    /**
     * Elasticsearch文档ID
     */
    private String embeddingId;

    /**
     * 引用的文本内容
     */
    private String text;

    /**
     * 相似度分数（0-1）
     */
    private Double score;
}
