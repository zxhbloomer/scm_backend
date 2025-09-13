/**
 * 级别枚举，定义系统中的层级关系
 */
package com.xinyirun.scm.ai.enums;

public enum LevelEnum {
    PLATFORM,
    ORGANIZATION,
    DEPARTMENT,
    WORKGROUP,
    AGENT,
    ROBOT,
    GROUP,
    USER;

    // 根据字符串查找对应的枚举常量
    public static LevelEnum fromValue(String value) {
        for (LevelEnum type : LevelEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No QuickReplyLevelEnum constant with value: " + value);
    }
}
