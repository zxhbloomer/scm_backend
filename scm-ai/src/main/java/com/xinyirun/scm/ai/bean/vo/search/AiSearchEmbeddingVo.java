package com.xinyirun.scm.ai.bean.vo.search;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI搜索向量VO类
 * 对应实体类:AiSearchEmbeddingEntity
 *
 * 注意:实际向量数据存储在Elasticsearch中,此VO仅包含元数据
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiSearchEmbeddingVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 向量ID(对应Elasticsearch文档ID)
     */
    private String embeddingId;

    /**
     * 搜索记录ID
     */
    private Long searchRecordId;

    /**
     * 文本内容
     */
    private String text;

    /**
     * 元数据(JSON格式)
     */
    private Map<String, Object> metadata;

    /**
     * Elasticsearch索引名称
     */
    private String esIndexName;

    /**
     * 创建时间
     */
    private LocalDateTime cTime;
}
