package com.xinyirun.scm.common.utils.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.xinyirun.scm.common.utils.datasource.properties.DataSourceProperties;
import com.xinyirun.scm.common.utils.spring.SpringHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * @note 数据源工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSourceHelper {
    private static final DataSource dataSource = SpringHelper.getBean(DataSource.class);
    private static final DefaultDataSourceCreator defaultDataSourceCreator = SpringHelper.getBean(DefaultDataSourceCreator.class);

    /**
     * 获取指定的数据源信息
     *
     * @param dataSourceId 数据源名字
     */
    public static DataSource getDataSource(String dataSourceId) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        return ds.getDataSource(dataSourceId);
    }

    /**
     * 获取所有数据源信息
     */
    public static Map<String, DataSource> getDataSource() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        return ds.getDataSources();
    }

    /**
     * 添加一个数据源
     */
    public static void addDataSource(DataSourceProperties dataSourceProperties) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource newDataSource = defaultDataSourceCreator.createDataSource(dataSourceProperties);
        ds.addDataSource(dataSourceProperties.getPoolName(), newDataSource);
    }

    /**
     * 删除一个数据源
     */
    public static void removeDataSource(String dataSourceId) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(dataSourceId);
    }

    /**
     * 设置当前线程数据源，与数据库交互前可以切换数据源
     *
     * @param dataSourceId 数据源名字
     */
    public static void use(String dataSourceId) {
        DynamicDataSourceContextHolder.push(dataSourceId);
    }

    /**
     * 获取当前线程数据源
     *
     * @return 数据源名字
     */
    public static String getCurrentDataSourceName() {
        if (DynamicDataSourceContextHolder.peek() == null) {
            return "master";
        } else {
            return DynamicDataSourceContextHolder.peek();
        }
    }

    /**
     * 关闭当前线程数据源
     */
    public static void close() {
        DynamicDataSourceContextHolder.clear();
    }

    /**
     * 测试连接
     *
     * @param dataSourceProperty 数据源属性
     * @return 连接是否成功
     */
    public static boolean connect(DataSourceProperty dataSourceProperty) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(dataSourceProperty.getUsername());
        druidDataSource.setPassword(dataSourceProperty.getPassword());
        druidDataSource.setUrl(dataSourceProperty.getUrl());
        druidDataSource.setDriverClassName(dataSourceProperty.getDriverClassName());
        return connect(druidDataSource);
    }

    /**
     * 测试连接
     *
     * @param dataSource 数据源
     * @return 连接是否成功
     */
    public static boolean connect(DataSource dataSource) {
        try (Connection ignored = dataSource.getConnection()) {
            log.info("连接成功");
        } catch (Exception e) {
            log.info("连接失败：{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 获取数据库驱动类路径
     *
     * @param datasourceType 数据源类型
     * @return 数据库驱动类路径
     */
    public static String getDriverClass(String datasourceType) {
        return "com.mysql.cj.jdbc.Driver";
    }

}
