package com.xinyirun.scm.ai.bean.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 高级设置参数业务视图对象
 *
 * 用于AI模型的高级参数配置传输
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AdvSettingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 参数类型
     */
    @Schema(description = "参数类型")
    private String name;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String label;

    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private Object value;

    /**
     * 参数最大值
     */
    @Schema(description = "参数最大值")
    private Object maxValue;

    /**
     * 参数最小值
     */
    @Schema(description = "参数最小值")
    private Object minValue;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 构造函数
     */
    public AdvSettingVo(String name, String label, Object value, Boolean enable) {
        this.name = name;
        this.label = label;
        this.value = value;
        this.enable = enable;
    }
}