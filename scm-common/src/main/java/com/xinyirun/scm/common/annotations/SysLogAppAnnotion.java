package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 系统App日志注解
 * @author zhangxh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SysLogAppAnnotion {
    String value() default "";
}