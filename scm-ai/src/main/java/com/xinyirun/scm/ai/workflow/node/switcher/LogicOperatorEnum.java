package com.xinyirun.scm.ai.workflow.node.switcher;

/**
 * 逻辑运算符枚举
 * 对齐AIDeepin: com.moyz.adi.common.workflow.node.switcher.LogicOperatorEnum
 *
 * @author SCM-AI团队
 * @since 2025-10-23
 */
public enum LogicOperatorEnum {
    AND("and"),

    OR("or");

    private final String name;

    LogicOperatorEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
