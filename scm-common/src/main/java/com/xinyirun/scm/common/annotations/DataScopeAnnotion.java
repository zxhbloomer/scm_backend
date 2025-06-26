package com.xinyirun.scm.common.annotations;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 * 
 * @author zxh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScopeAnnotion
{
    /**
     * 关于数据权限的分类：'01': 仓库组权限，and exists ()
     *                  '02': 仓库权限调整 and ( exists or exists)
     */
    public String type() default "";

    /**
     * 仓库权限:与主表的链接条件
     */
    public String type01_condition() default "";

    /**
     * 仓库权限:与主表的链接条件，先考虑两个（按逗号分割），因为入库，出库
     */
    public String type02_condition() default "";

}
