package com.xinyirun.scm.ai.common.util;

import java.util.Locale;

public class Translator {

    /**
     * 单Key翻译（无多语言支持，直接返回key）
     */
    public static String get(String key) {
        return key;
    }

    /**
     * 单Key翻译，并设置默认值
     */
    public static String get(String key, String defaultMessage) {
        return defaultMessage != null ? defaultMessage : key;
    }

    /**
     * 单Key翻译，并指定默认语言（无多语言支持，直接返回key）
     */
    public static String get(String key, Locale locale) {
        return key;
    }

    /**
     * 带参数（无多语言支持，直接返回key）
     */
    public static String getWithArgs(String key, Object... args) {
        return key;
    }

}