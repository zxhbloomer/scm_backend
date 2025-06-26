package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 系统api日志注解
 * @author zhangxh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SysLogApiAnnotion {
    String value() default "";

    /**
     * false 为 默认值, 是包含参数的
     * true 为 不包含参数的
     * @return
     */
    boolean noParam() default false;
}