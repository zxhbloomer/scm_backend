package com.xinyirun.scm.ai.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AI会话ID生成器
 *
 * 负责生成AI会话相关的各种ID
 * 包括会话ID、会话内容ID等，确保ID的唯一性和可读性
 *
 * @author zxh
 * @since 1.0.0
 */
public class AiConversationIdGenerator {

    /**
     * 序列号计数器，用于生成递增序列
     */
    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成会话ID
     * 格式: CONV_yyyyMMddHHmmss_UUID前8位
     *
     * @return 会话ID
     */
    public static String generateConversationId() {
        String dateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return String.format("CONV_%s_%s", dateTime, uuidPart);
    }

    /**
     * 生成会话内容ID
     * 格式: CONTENT_yyyyMMddHHmmss_序列号_UUID前6位
     *
     * @return 会话内容ID
     */
    public static String generateContentId() {
        String dateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        long sequence = SEQUENCE.incrementAndGet();
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return String.format("CONTENT_%s_%06d_%s", dateTime, sequence, uuidPart);
    }

    /**
     * 生成简单UUID（32位）
     * 兼容SCM项目现有的UUID生成规范
     *
     * @return 32位的UUID字符串
     */
    public static String generateSimpleUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /**
     * 生成带前缀的ID
     * 格式: prefix_yyyyMMddHHmmss_UUID前8位
     *
     * @param prefix ID前缀
     * @return 带前缀的ID
     */
    public static String generateIdWithPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "ID";
        }
        String dateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return String.format("%s_%s_%s", prefix.toUpperCase(), dateTime, uuidPart);
    }

    /**
     * 生成会话标题ID（用于临时标题生成）
     * 格式: TITLE_时间戳
     *
     * @return 会话标题ID
     */
    public static String generateTitleId() {
        return String.format("TITLE_%d", System.currentTimeMillis());
    }

    /**
     * 验证ID格式是否有效
     *
     * @param id 待验证的ID
     * @return true如果ID格式有效，false否则
     */
    public static boolean isValidId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        // 检查基本长度要求
        if (id.length() < 10) {
            return false;
        }

        // 检查是否包含非法字符（只允许字母、数字、下划线、短横线）
        return id.matches("^[A-Za-z0-9_-]+$");
    }

    /**
     * 从会话ID中提取创建日期
     *
     * @param conversationId 会话ID
     * @return 创建日期字符串，格式为yyyyMMddHHmmss，如果解析失败返回null
     */
    public static String extractDateFromConversationId(String conversationId) {
        try {
            if (conversationId != null && conversationId.startsWith("CONV_")) {
                String[] parts = conversationId.split("_");
                if (parts.length >= 2) {
                    return parts[1];
                }
            }
        } catch (Exception e) {
            // 忽略解析异常
        }
        return null;
    }

    /**
     * 重置序列号计数器（主要用于测试）
     */
    public static void resetSequence() {
        SEQUENCE.set(0);
    }
}