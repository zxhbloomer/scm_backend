package com.xinyirun.scm.ai.kbase.enums;

/**
 * 知识库状态枚举
 */
public enum KbaseStatusEnum {
    
    DRAFT("DRAFT", "草稿"),
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已审核"),
    PUBLISHED("PUBLISHED", "已发布"),
    ARCHIVED("ARCHIVED", "已归档"),
    REJECTED("REJECTED", "已拒绝"),
    DELETED("DELETED", "已删除");

    private final String value;
    private final String description;

    KbaseStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static KbaseStatusEnum fromValue(String value) {
        for (KbaseStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown KbaseStatusEnum value: " + value);
    }

    public static KbaseStatusEnum fromDescription(String description) {
        for (KbaseStatusEnum status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown KbaseStatusEnum description: " + description);
    }

    @Override
    public String toString() {
        return this.value;
    }
}