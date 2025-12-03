package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.Data;

/**
 * 知识库文档向量嵌入VO类
 *
 * <p>用于展示Milvus中存储的文档向量数据</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-09
 */
@Data
public class KbEmbeddingVo {

    /**
     * Milvus文档ID
     */
    private String id;

    /**
     * 知识库UUID
     */
    private String kbUuid;

    /**
     * 知识项UUID
     */
    private String kbItemUuid;

    /**
     * 文本内容片段
     */
    private String content;

    /**
     * 分块序号
     */
    private Integer chunkIndex;

    /**
     * Token数量
     */
    private Integer tokenCount;

    /**
     * 时间戳
     */
    private Long timestamp;
}
