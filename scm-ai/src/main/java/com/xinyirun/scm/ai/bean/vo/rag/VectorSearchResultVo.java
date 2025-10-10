package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 向量检索结果VO
 *
 * <p>对应aideepin的EmbeddingMatch<TextSegment>结果</p>
 * <p>参考：dev.langchain4j.data.embedding.EmbeddingMatch</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorSearchResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Elasticsearch文档ID
     * 对应aideepin的embeddingId
     */
    private String embeddingId;

    /**
     * 相似度分数（0-1之间，越高越相似）
     * 对应aideepin的score
     */
    private Double score;

    /**
     * 文本内容
     * 对应aideepin的TextSegment.text()
     */
    private String content;

    /**
     * 知识库UUID
     */
    private String kbUuid;

    /**
     * 知识库条目UUID
     */
    private String kbItemUuid;

    /**
     * 文本段索引（在文档中的位置）
     */
    private Integer segmentIndex;

    /**
     * 元数据
     * 包含文件名、租户ID等扩展信息
     */
    private Map<String, Object> metadata;
}
