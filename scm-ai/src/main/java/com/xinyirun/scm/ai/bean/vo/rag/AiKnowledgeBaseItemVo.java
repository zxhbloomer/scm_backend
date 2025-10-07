package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI知识库文档项VO类
 *
 * <p>与AiKnowledgeBaseItemEntity一一对应</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class AiKnowledgeBaseItemVo {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 文档UUID（业务主键）
     */
    private String itemUuid;

    /**
     * 所属知识库UUID
     */
    private String kbUuid;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 简介（文档摘要）
     */
    private String brief;

    /**
     * 备注（文档完整内容）
     */
    private String remark;

    /**
     * 源文件名称
     */
    private String sourceFileName;

    /**
     * 向量化状态(1-待处理,2-处理中,3-已完成,4-失败)
     */
    private Integer embeddingStatus;

    /**
     * 创建时间（时间戳毫秒）
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUser;
}
