package com.xinyirun.scm.common.annotations.bpm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 字段元数据注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMeta {
    String title();                  // 字段的定义名称
    boolean required() default false; // 是否必填
    String fieldType() default "";    // 字段类型
    String valueType() default "";    // 字段值类型
    String dicType() default "";      // 字段字典类型
}