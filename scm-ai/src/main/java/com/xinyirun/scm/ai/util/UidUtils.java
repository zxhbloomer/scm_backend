package com.xinyirun.scm.ai.util;

import java.util.UUID;

/**
 * UID工具类
 */
public class UidUtils {

    /**
     * 生成随机UID
     * 
     * @return 32位随机字符串UID
     */
    public static String generateUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成UUID（别名方法，兼容旧代码）
     * 
     * @return 32位随机字符串UID
     */
    public static String uuid() {
        return generateUid();
    }

    /**
     * 生成带前缀的UID
     * 
     * @param prefix 前缀
     * @return 带前缀的UID
     */
    public static String generateUidWithPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return generateUid();
        }
        return prefix.trim() + "_" + generateUid();
    }

    /**
     * 验证UID格式是否有效
     * 
     * @param uid UID字符串
     * @return 是否有效
     */
    public static boolean isValidUid(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            return false;
        }
        
        // 基本格式检查：应该是32位字符串或带前缀的格式
        String trimmedUid = uid.trim();
        if (trimmedUid.length() == 32) {
            // 32位UUID格式检查
            return trimmedUid.matches("[a-fA-F0-9]{32}");
        } else if (trimmedUid.contains("_")) {
            // 带前缀的格式检查
            String[] parts = trimmedUid.split("_", 2);
            if (parts.length == 2 && parts[1].length() == 32) {
                return parts[1].matches("[a-fA-F0-9]{32}");
            }
        }
        
        return false;
    }

    /**
     * 从带前缀的UID中提取纯UID部分
     * 
     * @param prefixedUid 带前缀的UID
     * @return 纯UID部分
     */
    public static String extractUid(String prefixedUid) {
        if (prefixedUid == null || prefixedUid.trim().isEmpty()) {
            return null;
        }
        
        String trimmedUid = prefixedUid.trim();
        if (trimmedUid.contains("_")) {
            String[] parts = trimmedUid.split("_", 2);
            if (parts.length == 2) {
                return parts[1];
            }
        }
        
        return trimmedUid;
    }

    /**
     * 从带前缀的UID中提取前缀部分
     * 
     * @param prefixedUid 带前缀的UID
     * @return 前缀部分，如果没有前缀则返回null
     */
    public static String extractPrefix(String prefixedUid) {
        if (prefixedUid == null || prefixedUid.trim().isEmpty()) {
            return null;
        }
        
        String trimmedUid = prefixedUid.trim();
        if (trimmedUid.contains("_")) {
            String[] parts = trimmedUid.split("_", 2);
            if (parts.length == 2) {
                return parts[0];
            }
        }
        
        return null;
    }

    /**
     * 生成知识库专用UID
     * 
     * @return 带kb前缀的UID
     */
    public static String generateKbaseUid() {
        return generateUidWithPrefix("kb");
    }

    /**
     * 生成分类专用UID
     * 
     * @return 带cat前缀的UID
     */
    public static String generateCategoryUid() {
        return generateUidWithPrefix("cat");
    }

    /**
     * 生成线程专用UID
     * 
     * @return 带thread前缀的UID
     */
    public static String generateThreadUid() {
        return generateUidWithPrefix("thread");
    }

    /**
     * 生成消息专用UID
     * 
     * @return 带msg前缀的UID
     */
    public static String generateMessageUid() {
        return generateUidWithPrefix("msg");
    }

    /**
     * 生成用户专用UID
     * 
     * @return 带user前缀的UID
     */
    public static String generateUserUid() {
        return generateUidWithPrefix("user");
    }

    /**
     * 生成组织专用UID
     * 
     * @return 带org前缀的UID
     */
    public static String generateOrgUid() {
        return generateUidWithPrefix("org");
    }
}