package com.xinyirun.scm.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Collection;

/**
 * @ClassName: ArrayUtil
 * @Description: 数组工具类
 * @Author: zxh
 * @date: 2020/3/2
 * @Version: 1.0
 */
public class ArrayPfUtil {

    /**
     * 判断是否为空
     * @param array
     * @return
     */
    public static boolean isEmpty(Object array) {
        return ArrayUtil.isNotEmpty(array);
    }

    /**
     * 判断数组是否为空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return CollUtil.isNotEmpty(collection);
    }

    /**
     * 判断数组是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollUtil.isEmpty(collection);
    }
}
