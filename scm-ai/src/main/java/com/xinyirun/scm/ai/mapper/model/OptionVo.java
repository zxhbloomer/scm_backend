package com.xinyirun.scm.ai.mapper.model;

import lombok.Data;

/**
 * 选项VO类
 * 用于下拉选择框等场景的简单键值对数据传输
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
public class OptionVo {

    /**
     * 选项值
     */
    private Integer value;

    /**
     * 选项文本
     */
    private String text;

    public OptionVo() {}

    public OptionVo(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
}