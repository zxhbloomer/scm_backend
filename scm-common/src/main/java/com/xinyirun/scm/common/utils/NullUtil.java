package com.xinyirun.scm.common.utils;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @ClassName: NullUtil
 * @Description: NullUtil
 * @Author: zxh
 * @date: 2020/4/13
 * @Version: 1.0
 */
public class NullUtil {
    public static Boolean isNull(Object object) {
        return !ObjectUtils.allNotNull(object);
    }
}
