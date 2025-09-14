package com.xinyirun.scm.ai.kbase.enums;

/**
 * 知识库类型枚举
 */
public enum KbaseTypeEnum {
    
    TEXT("TEXT", "文本"),
    FILE("FILE", "文件"),
    URL("URL", "链接"),
    FAQ("FAQ", "常见问题"),
    QA("QA", "问答"),
    DOCUMENT("DOCUMENT", "文档"),
    MANUAL("MANUAL", "手册"),
    POLICY("POLICY", "政策"),
    PROCEDURE("PROCEDURE", "流程");

    private final String value;
    private final String description;

    KbaseTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static KbaseTypeEnum fromValue(String value) {
        for (KbaseTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown KbaseTypeEnum value: " + value);
    }

    public static KbaseTypeEnum fromDescription(String description) {
        for (KbaseTypeEnum type : values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown KbaseTypeEnum description: " + description);
    }

    @Override
    public String toString() {
        return this.value;
    }
}