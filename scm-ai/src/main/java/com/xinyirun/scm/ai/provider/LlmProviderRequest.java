/*
 * SCM AI Module - LLM Provider Request
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商请求对象，用于API请求参数绑定
 */
package com.xinyirun.scm.ai.provider;

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
 * AI提供商请求对象
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
public class LlmProviderRequest extends BaseVo implements Serializable {

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
     * 提供商名称
     */
    private String name;

    /**
     * 提供商昵称
     */
    private String nickname;

    /**
     * 提供商Logo URL
     */
    private String logo;
    
    /**
     * 提供商描述
     */
    @Builder.Default
    private String description = "";

    /**
     * API基础URL
     */
    private String baseUrl;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 官网地址
     */
    private String webUrl;
    
    /**
     * 提供商状态
     */
    private String status;

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