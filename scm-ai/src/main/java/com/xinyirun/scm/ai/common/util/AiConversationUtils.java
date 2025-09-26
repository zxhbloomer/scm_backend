package com.xinyirun.scm.ai.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI会话工具类
 *
 * 提供AI会话相关的通用工具方法
 * 包括内容处理、格式化、验证等功能
 *
 * @author zxh
 * @since 1.0.0
 */
public class AiConversationUtils {

    /**
     * 默认的会话标题长度限制
     */
    public static final int DEFAULT_TITLE_MAX_LENGTH = 100;

    /**
     * 默认的内容长度限制（20MB）
     */
    public static final int DEFAULT_CONTENT_MAX_LENGTH = 20 * 1024 * 1024;

    /**
     * 默认的摘要长度
     */
    public static final int DEFAULT_SUMMARY_LENGTH = 200;

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * URL正则表达式
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://[\\w\\-\\.]+(?:\\.[a-zA-Z]{2,})+(?:/[\\w\\-\\._~:/?#\\[\\]@!$&'()*+,;=]*)?",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * 敏感词汇列表（示例）
     */
    private static final List<String> SENSITIVE_WORDS = List.of(
        "密码", "password", "token", "secret", "私钥", "private_key"
    );

    /**
     * 清理和格式化会话内容
     *
     * @param content 原始内容
     * @return 清理后的内容
     */
    public static String cleanContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }

        // 移除多余的空白字符
        content = content.trim();

        // 移除连续的空行，最多保留两个换行符
        content = content.replaceAll("\n{3,}", "\n\n");

        // 移除行尾空格
        content = content.replaceAll("[ \t]+\n", "\n");

        return content;
    }

    /**
     * 生成内容摘要
     *
     * @param content 原始内容
     * @param maxLength 最大摘要长度
     * @return 内容摘要
     */
    public static String generateSummary(String content, int maxLength) {
        if (StringUtils.isBlank(content)) {
            return "";
        }

        if (maxLength <= 0) {
            maxLength = DEFAULT_SUMMARY_LENGTH;
        }

        // 清理内容
        String cleanedContent = cleanContent(content);

        // 如果内容长度小于等于最大长度，直接返回
        if (cleanedContent.length() <= maxLength) {
            return cleanedContent;
        }

        // 截取并添加省略号
        String summary = cleanedContent.substring(0, maxLength - 3);

        // 尝试在词汇边界处截断
        int lastSpace = summary.lastIndexOf(' ');
        int lastNewline = summary.lastIndexOf('\n');
        int cutPoint = Math.max(lastSpace, lastNewline);

        if (cutPoint > maxLength / 2) {
            summary = summary.substring(0, cutPoint);
        }

        return summary + "...";
    }

    /**
     * 生成默认会话标题
     *
     * @param content 会话内容
     * @return 默认标题
     */
    public static String generateDefaultTitle(String content) {
        if (StringUtils.isBlank(content)) {
            return "新会话 " + LocalDateTime.now().format(DATETIME_FORMATTER);
        }

        // 提取内容的前几个词作为标题
        String cleanedContent = cleanContent(content);
        String[] words = cleanedContent.split("\\s+");

        StringBuilder titleBuilder = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            if (wordCount >= 8) { // 最多8个词
                break;
            }

            if (titleBuilder.length() + word.length() + 1 > DEFAULT_TITLE_MAX_LENGTH - 10) {
                break;
            }

            if (titleBuilder.length() > 0) {
                titleBuilder.append(" ");
            }
            titleBuilder.append(word);
            wordCount++;
        }

        String title = titleBuilder.toString();
        if (title.length() > DEFAULT_TITLE_MAX_LENGTH) {
            title = title.substring(0, DEFAULT_TITLE_MAX_LENGTH - 3) + "...";
        }

        return StringUtils.isNotBlank(title) ? title : "新会话 " + LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * 验证内容是否包含敏感信息
     *
     * @param content 待验证的内容
     * @return true如果包含敏感信息，false否则
     */
    public static boolean containsSensitiveInfo(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        String lowerContent = content.toLowerCase();
        return SENSITIVE_WORDS.stream().anyMatch(lowerContent::contains);
    }

    /**
     * 屏蔽敏感信息
     *
     * @param content 原始内容
     * @return 屏蔽敏感信息后的内容
     */
    public static String maskSensitiveInfo(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        String maskedContent = content;
        for (String sensitiveWord : SENSITIVE_WORDS) {
            maskedContent = maskedContent.replaceAll("(?i)" + Pattern.quote(sensitiveWord), "***");
        }

        return maskedContent;
    }

    /**
     * 提取内容中的URL链接
     *
     * @param content 内容
     * @return URL列表
     */
    public static List<String> extractUrls(String content) {
        List<String> urls = new ArrayList<>();
        if (StringUtils.isBlank(content)) {
            return urls;
        }

        Matcher matcher = URL_PATTERN.matcher(content);
        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }

    /**
     * 验证会话内容是否有效
     *
     * @param content 会话内容
     * @return true如果有效，false否则
     */
    public static boolean isValidContent(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        // 检查长度限制
        if (content.length() > DEFAULT_CONTENT_MAX_LENGTH) {
            return false;
        }

        // 检查是否只包含空白字符
        if (content.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 验证会话标题是否有效
     *
     * @param title 会话标题
     * @return true如果有效，false否则
     */
    public static boolean isValidTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return false;
        }

        // 检查长度限制
        if (title.length() > DEFAULT_TITLE_MAX_LENGTH) {
            return false;
        }

        // 检查是否包含非法字符
        return !title.matches(".*[<>\"'&].*");
    }

    /**
     * 转换对象为JSON字符串（安全版本）
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "{}";
        }

        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * 从JSON字符串解析对象（安全版本）
     *
     * @param jsonString JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 解析后的对象，解析失败返回null
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString) || clazz == null) {
            return null;
        }

        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算文本的简单哈希值（用于去重）
     *
     * @param text 文本内容
     * @return 哈希值
     */
    public static int calculateSimpleHash(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }

        return text.trim().hashCode();
    }

    /**
     * 格式化时间为字符串
     *
     * @param dateTime 时间对象
     * @return 格式化后的时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 获取当前时间的格式化字符串
     *
     * @return 当前时间字符串
     */
    public static String getCurrentTimeString() {
        return formatDateTime(LocalDateTime.now());
    }

    /**
     * 截断文本到指定长度
     *
     * @param text 原始文本
     * @param maxLength 最大长度
     * @param addEllipsis 是否添加省略号
     * @return 截断后的文本
     */
    public static String truncateText(String text, int maxLength, boolean addEllipsis) {
        if (StringUtils.isBlank(text) || maxLength <= 0) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        if (addEllipsis && maxLength > 3) {
            return text.substring(0, maxLength - 3) + "...";
        } else {
            return text.substring(0, maxLength);
        }
    }
}