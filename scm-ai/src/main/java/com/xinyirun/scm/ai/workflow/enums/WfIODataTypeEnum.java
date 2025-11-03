package com.xinyirun.scm.ai.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 工作流输入输出数据类型枚举
 *
 * @author zxh
 * @since 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum WfIODataTypeEnum {
    TEXT(1, "文本"),
    NUMBER(2, "数字"),
    OPTIONS(3, "下拉选项"),
    FILES(4, "文件列表"),
    BOOL(5, "布尔值"),
    REF_INPUT(6, "引用节点的输入参数"),
    REF_OUTPUT(7, "引用节点的输出参数");

    private final Integer value;
    private final String desc;

    public static WfIODataTypeEnum getByValue(Integer val) {
        return Arrays.stream(WfIODataTypeEnum.values())
                .filter(item -> item.value.equals(val))
                .findFirst()
                .orElse(null);
    }
}
