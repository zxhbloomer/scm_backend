/*
 * SCM AI Module - ByteDesk Compatible Base Entity
 * Adapted from ByteDesk BaseEntity for SCM System with MyBatis Plus
 * 
 * Author: SCM Development Team
 * Description: ByteDesk风格的基础实体类，使用MyBatis Plus注解适配
 */
package com.xinyirun.scm.ai.base;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * ByteDesk兼容的基础实体类
 * 保持与ByteDesk BaseEntity字段结构一致，但使用MyBatis Plus注解
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BytedeskBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务唯一标识符
     * 对应ByteDesk中的uuid字段
     */
    @TableField("uid")
    private String uid;
    
    /**
     * 乐观锁版本字段
     */
    @Version
    @TableField("version")
    private Integer version;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记
     */
    @Builder.Default
    @TableLogic
    @TableField("is_deleted")
    private Boolean deleted = false;

    /**
     * 组织UID
     */
    @TableField("org_uid")
    private String orgUid;

    /**
     * 用户UID
     */
    @TableField("user_uid") 
    private String userUid;

    /**
     * 级别（ORGANIZATION/PLATFORM等）
     */
    @Builder.Default
    @TableField("level")
    private String level = "ORGANIZATION";

    /**
     * 平台标识
     */
    @Builder.Default
    @TableField("platform")
    private String platform = "SCM";

    /**
     * 获取创建时间字符串格式
     * 
     * @return 格式化的创建时间
     */
    public String getCreatedAtString() {
        return createdAt != null ? createdAt.toString() : null;
    }

    /**
     * 获取更新时间字符串格式
     * 
     * @return 格式化的更新时间
     */
    public String getUpdatedAtString() {
        return updatedAt != null ? updatedAt.toString() : null;
    }
}