package com.xinyirun.scm.common.annotations;

import com.xinyirun.scm.common.enums.datasource.DataSourceTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义多数据源切换注解
 * 
 * @author
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceAnnotion {
    /**
     * 切换数据源名称
     */
    DataSourceTypeEnum value() default DataSourceTypeEnum.MASTER;
}
