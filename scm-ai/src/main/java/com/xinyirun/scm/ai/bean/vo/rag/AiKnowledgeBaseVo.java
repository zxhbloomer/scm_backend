package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI知识库VO类
 *
 * <p>与AiKnowledgeBaseEntity一一对应</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class AiKnowledgeBaseVo {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 知识库UUID（业务主键）
     */
    private String kbUuid;

    /**
     * 知识库标题
     */
    private String title;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 是否公开（0-私有，1-公开）
     */
    private Integer isPublic;

    /**
     * 所有者ID
     */
    private String ownerId;

    /**
     * 所有者名称
     */
    private String ownerName;

    /**
     * 文档项数量
     */
    private Integer itemCount;

    /**
     * 总Token数量
     */
    private Long totalTokens;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间（时间戳毫秒）
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUser;
}
