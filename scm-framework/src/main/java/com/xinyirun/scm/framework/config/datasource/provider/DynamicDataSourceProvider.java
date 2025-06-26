package com.xinyirun.scm.framework.config.datasource.provider;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.xinyirun.scm.common.utils.datasource.properties.DataSourceProperties;
import com.xinyirun.scm.framework.config.datasource.constant.DataSourceConstant;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @note 项目初始化时，从数据库中获取数据源信息进行加载
 */
@Slf4j
public class DynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {
    private final DataSourceProperties dataSourceProperties;
    private final boolean loadDefaultDataSource;

    public DynamicDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator, DataSourceProperties dataSourceProperties, boolean loadDefaultDataSource) {
        super(defaultDataSourceCreator, dataSourceProperties.getDriverClassName(), dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
        this.dataSourceProperties = dataSourceProperties;
        this.loadDefaultDataSource = loadDefaultDataSource;
    }

    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        Map<String, DataSourceProperty> map = new HashMap<>(8);

        // 添加默认主数据源
        if (loadDefaultDataSource) {
            map.put(DataSourceConstant.DS_MASTER, dataSourceProperties);
        }

        String queryDsSql = dataSourceProperties.getTenent_sql();

        ResultSet rs = statement.executeQuery(queryDsSql);
        while (rs.next()) {
            String name = rs.getString(DataSourceConstant.DS_CODE);
            String username = rs.getString(DataSourceConstant.DS_USER_NAME);
            String password = rs.getString(DataSourceConstant.DS_USER_PWD);
            String url = rs.getString(DataSourceConstant.DS_JDBC_URL);
            String driverClassName =  "com.mysql.cj.jdbc.Driver";
            DataSourceProperty property = new DataSourceProperty();
            property.setUsername(username);
            property.setLazy(false);
            property.setPassword(password);
            property.setUrl(url);
            property.setDriverClassName(driverClassName);

            // 默认使用 Druid 连接池
            property.setType(DruidDataSource.class);
            map.put(name, property);
        }
        return map;
    }
}