package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 数据变动记录注解：操作日志记录
 * @author zhangxh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataChangeOperateAnnotation {
    /**
     * 页面名称，中文
     * @return
     */
    String page_name() default "";

    /**
     * 操作日志
     * @return
     */
    String value() default "";

}