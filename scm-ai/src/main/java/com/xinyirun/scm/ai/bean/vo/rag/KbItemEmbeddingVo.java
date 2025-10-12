package com.xinyirun.scm.ai.bean.vo.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库文档项嵌入向量VO类
 *
 * @author SCM AI Team
 * @since 2025-10-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库文档项嵌入向量VO")
public class KbItemEmbeddingVo {

    /**
     * 嵌入向量ID
     */
    @Schema(description = "嵌入向量ID")
    private String embeddingId;

    /**
     * 向量数组(1024维)
     */
    @Schema(description = "向量数组(1024维)")
    private float[] embedding;

    /**
     * 文档片段内容
     */
    @Schema(description = "文档片段内容")
    private String text;
}
