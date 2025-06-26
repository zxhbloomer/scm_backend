package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 系统日志注解
 * @author zhangxh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SysLogServiceAnnotation {
    String value() default "";
}