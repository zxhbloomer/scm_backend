package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 数据变动记录注解：名称
 * @author zhangxh
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataChangeLabelAnnotation {
    String value();
    // 必须显示类型：false

    /**
     * false：如果比对前后一致，不显示
     * true：无论是否一致，都需要显示
     * @return
     */
    boolean fixed() default false;

    /**
     * 扩展功能：
     * 1、写一下扩展功能的function名称，classname=DataChangeEntityAnnotation.type
     * 2、参数为当前字段的值
     * @return
     */
    String extension() default "";

    /**
     * 字典扩展功能：
     * 1、写一下扩展功能的function名称，classname=DataChangeEntityAnnotation.type
     * 2、参数为当前字段的值
     * @return
     */
    String dictExtension() default "";

    /**
     * 字典扩展功能：
     * 1、写一下扩展功能的function名称，classname=DataChangeEntityAnnotation.type
     * 2、参数为当前查询字典的类型参数DictConstant.DICT_TYPE_XXX
     * @return
     */
    String dictExtensionType() default "";
}
