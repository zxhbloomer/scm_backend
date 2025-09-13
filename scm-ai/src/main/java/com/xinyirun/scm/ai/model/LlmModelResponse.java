/*
 * SCM AI Module - LLM Model Response
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: LLM模型响应对象，用于API响应数据传输
 */
package com.xinyirun.scm.ai.model;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * LLM模型响应对象
 * 用于API响应数据传输
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LlmModelResponse extends BaseVo {

    private static final long serialVersionUID = 1L;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型昵称
     */
    private String nickname;

    /**
     * 模型描述
     */
    private String description;

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
}