package com.xinyirun.scm.ai.bean.constant;

/**
 * AI模型相关常量定义
 *
 * @author SCM-AI重构团队
 * @since 2025-09-30
 */
public class ModelConstants {

    /**
     * 系统级模型拥有者标识
     * 用于标识由系统创建和管理的公共模型
     */
    public static final String SYSTEM_OWNER = "system";

    /**
     * 权限类型枚举
     */
    public enum PermissionType {
        PUBLIC("PUBLIC", "公共模型"),
        PRIVATE("PRIVATE", "私有模型");

        private final String code;
        private final String description;

        PermissionType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public boolean isPublic() {
            return this == PUBLIC;
        }

        public boolean isPrivate() {
            return this == PRIVATE;
        }
    }

    /**
     * 拥有者类型枚举
     */
    public enum OwnerType {
        SYSTEM("system", "系统"),
        USER("user", "用户");

        private final String code;
        private final String description;

        OwnerType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public boolean isSystem() {
            return this == SYSTEM;
        }

        public boolean isUser() {
            return this == USER;
        }
    }
}