package com.xinyirun.scm.ai.common.util;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * <p>严格对标aideepin的AdiStringUtil工具类</p>
 *
 * @author SCM AI Team
 * @since 2025-10-16
 */
public class AdiStringUtil {

    /**
     * 清除HTML标签,提取纯文本
     * 对应aideepin AdiStringUtil.clearStr()
     *
     * @param str 原始字符串(可能包含HTML标签)
     * @return 纯文本内容
     */
    public static String clearStr(String str) {
        if (str == null) {
            return "";
        }
        org.jsoup.nodes.Document doc = Jsoup.parse(str);
        return doc.text();
    }

    /**
     * 截取字符串尾部指定长度
     * 对应aideepin AdiStringUtil.tail()
     *
     * <p>用于实体名称长度限制(默认20字符)</p>
     * <p>应用场景: Neo4j实体名称索引性能优化</p>
     *
     * <p>示例:</p>
     * <pre>
     * tail("中国工商银行股份有限公司深圳分行", 20) → "国工商银行股份有限公司深圳分行" (保留后20字符)
     * tail("Apple", 20) → "Apple" (长度不足,返回原字符串)
     * </pre>
     *
     * @param source 原始字符串
     * @param tailLength 尾部保留长度
     * @return 截取后的字符串
     */
    public static String tail(String source, int tailLength) {
        if (source == null) {
            return "";
        }
        if (source.length() <= tailLength) {
            return source;
        }
        return source.substring(source.length() - tailLength);
    }

    /**
     * 支持将字符串按分隔符切割并转换为List,支持基础类型对应的字符串
     * 对应aideepin AdiStringUtil.stringToList()
     *
     * @param str       待转换的字符串
     * @param separator 分隔符
     * @param function  转换函数,将字符串转换为目标类型
     * @param <T>       列表元素类型
     * @return 转换后的List
     */
    public static <T> List<T> stringToList(String str, String separator, Function<String, T> function) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        String[] parts = str.split(separator);
        List<T> result = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                result.add(function.apply(part));
            }
        }
        return result;
    }

    /**
     * 移除特殊字符(保留中文、字母、数字、空格)
     * 严格对标aideepin AdiStringUtil.removeSpecialChar()
     *
     * <p>aideepin原逻辑(第48-54行):</p>
     * <pre>
     * public static String removeSpecialChar(String input) {
     *     String regEx = "[\\-`~!@#$%^&*()+=|{}':;,.<>/?！￥…（）—【】'；：""'。，、？]";
     *     Pattern p = Pattern.compile(regEx);
     *     Matcher m = p.matcher(input);
     *     return m.replaceAll("").trim();
     * }
     * </pre>
     *
     * <p>应用场景: 图谱实体名称标准化</p>
     *
     * <p>示例:</p>
     * <pre>
     * removeSpecialChar("苹果公司（Apple Inc.）") → "苹果公司Apple Inc"
     * removeSpecialChar("价格:¥999.00") → "价格999.00"
     * removeSpecialChar("张三@公司") → "张三公司"
     * </pre>
     *
     * @param input 原始字符串
     * @return 移除特殊字符后的字符串(已去除首尾空格)
     */
    public static String removeSpecialChar(String input) {
        // 匹配常见特殊符号（包括中英文符号）
        String regEx = "[\\-`~!@#$%^&*()+=|{}':;,.<>/?！￥…（）—【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(input);
        return m.replaceAll("").trim(); // 替换为空字符串并去除首尾空格
    }
}
