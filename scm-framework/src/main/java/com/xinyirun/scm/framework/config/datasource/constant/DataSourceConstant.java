package com.xinyirun.scm.framework.config.datasource.constant;

/**
 * @author Kele-Bingtang
 * @date 2023/12/26 21:06
 * @note
 */
public interface DataSourceConstant {
    /**
     * 数据源id
     */
    String DS_ID = "id";

    /**
     * 数据源名称（存储数据源名称的字段名）
     */
    String DS_CODE = "tenant";

    /**
     * 数据源名称（存储数据源名称的字段名）
     */
    String DS_TYPE = "data_source_type";

    /**
     * 默认数据源（master）
     */
    String DS_MASTER = "master";

    /**
     * JDBC URL（存储 JDBC URL 的字段名）
     */
    String DS_JDBC_URL = "url";

    /**
     * 用户名（存储用户名的字段名）
     */
    String DS_USER_NAME = "user_name";

    /**
     * 密码（存储密码的字段名）
     */
    String DS_USER_PWD = "password";

    /**
     * 驱动包名称
     */
    String DS_DRIVER_CLASS_NAME = "driver_class_name";

}