/*
 * SCM AI Module - LLM Provider Response
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商响应对象，用于API响应数据传输
 */
package com.xinyirun.scm.ai.provider;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AI提供商响应对象
 * 用于API响应数据传输
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class LlmProviderResponse extends BaseVo {

    private static final long serialVersionUID = 1L;
    
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
    private String description;
    
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
}