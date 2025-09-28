package com.xinyirun.scm.ai.bean.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 选项VO
 *
 * 用于下拉选项数据传输，替代原有的OptionDTO类
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项ID
     */
    @Schema(description = "选项ID")
    private String id;

    /**
     * 选项名称
     */
    @Schema(description = "选项名称")
    private String name;
}