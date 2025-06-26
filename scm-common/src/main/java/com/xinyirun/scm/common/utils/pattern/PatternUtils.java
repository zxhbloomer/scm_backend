package com.xinyirun.scm.common.utils.pattern;

import java.util.regex.Pattern;

/**
 * @author Wang Qianfeng
 * @Description 正则表达式工具嘞
 * @date 2023/3/3 15:56
 */
public class PatternUtils {

    /**
     * mongodb 模糊查询正则表达式
     */
    public static Pattern regexPattern(String string) {
        return Pattern.compile("^.*" + string + ".*$", Pattern.CASE_INSENSITIVE);
    }
}
