package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 知识库收藏记录实体类
 *
 * <p>对应数据库表：ai_knowledge_base_star</p>
 * 
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@TableName("ai_knowledge_base_star")
public class AiKnowledgeBaseStarEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 知识库ID
     */
    @TableField("kb_id")
    private String kbId;

    /**
     * 知识库UUID
     */
    @TableField("kb_uuid")
    private String kbUuid;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 创建时间（时间戳毫秒）
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;
}
