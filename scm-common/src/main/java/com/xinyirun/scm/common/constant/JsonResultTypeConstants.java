package com.xinyirun.scm.common.constant;

/**
 * @ClassName: JsonResultTypeConstants
 * @Description: jsonresult的类型常量
 * @Author: zxh
 * @date: 2020/9/4
 * @Version: 1.0
 */
public class JsonResultTypeConstants {

    /** 正常，null数据可以生成；boolean类型null->false；数组null转[] */
    public static final int NORMAL = 0;
    /** string null 则返回，空字符；boolean类型null->false */
    public static final int STRING_EMPTY_BOOLEAN_FALSE = 1;
    /** null数据不生成 */
    public static final int NULL_NOT_OUT = 2;
//    /** NORMAL & Null List Not out */
//    public static final int NORMAL_AND_NULL_LIST_NOT = 3;
}
