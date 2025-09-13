/*
 * SCM AI Module - LLM Model Request
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: LLM模型请求对象，用于API请求参数绑定
 */
package com.xinyirun.scm.ai.model;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * LLM模型请求对象
 * 用于API请求参数绑定和数据传输
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class LlmModelRequest extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID（用于更新和删除操作）
     */
    private Long id;
    
    /**
     * 业务UID（用于查询操作）
     */
    private String uid;

    /**
     * 模型名称，用于调用
     */
    private String name;
    
    /**
     * 便于记忆的昵称
     */
    private String nickname;

    /**
     * 模型描述
     */
    @Builder.Default
    private String description = "";

    /**
     * 模型类型
     */
    private String type;

    /**
     * 提供商UID
     */
    private String providerUid;

    /**
     * 提供商名称
     */
    private String providerName;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否系统级启用
     */
    private Boolean systemEnabled;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;
    
    /**
     * 查询类型（用于不同的查询场景）
     */
    private String queryType;
    
    /**
     * 检查类型（用于数据校验）
     */
    private String checkType;
}