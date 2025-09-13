/**
 * 平台枚举，定义支持的平台类型
 */
package com.xinyirun.scm.ai.enums;

public enum PlatformEnum {
    BYTEDESK,
    LIANGSHIBAO,
    ZHAOBIAO,
    MEIYU,
    TIKU;

    // 根据字符串查找对应的枚举常量
    public static PlatformEnum fromValue(String value) {
        for (PlatformEnum type : PlatformEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No PlatformEnum constant with value: " + value);
    }

}
