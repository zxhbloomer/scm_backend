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
 * @author SCM AI Team
 * @since 2025-10-16
 */
public class AdiStringUtil {


    /**
     * 截取字符串尾部指定长度
     *
     * <p>用于字符串长度限制(默认20字符)</p>
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
     * 移除特殊字符(保留中文、字母、数字、空格)
     *
     * <p>应用场景: 文本名称标准化</p>
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
