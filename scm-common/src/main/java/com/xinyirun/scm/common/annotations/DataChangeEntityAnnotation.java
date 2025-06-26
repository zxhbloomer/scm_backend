package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 数据变动记录注解
 * @author zhangxh
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataChangeEntityAnnotation {
    String value() default "";
    String type() default "";
}