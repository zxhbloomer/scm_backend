package com.xinyirun.scm.ai.bean.vo.response;

import lombok.Data;

/**
 * 模型源选项VO类
 * 专门用于模型源下拉选择框的键值对数据传输
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
public class ModelSourceOptionVo {

    /**
     * 选项值（模型源ID）
     */
    private Integer value;

    /**
     * 选项文本（模型源名称）
     */
    private String text;

    public ModelSourceOptionVo() {}

    public ModelSourceOptionVo(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
}