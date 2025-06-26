package com.xinyirun.scm.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DruidConnectionProvider类实现了Quartz的ConnectionProvider接口，
 * 用于提供数据库连接。这个类使用了Druid作为连接池。
 * 常量配置，与quartz.properties文件的key保持一致(去掉前缀)，同时提供set方法，Quartz框架自动注入值。
 */
@Data
public class DruidConnectionProvider implements ConnectionProvider {
    /**
     * 数据库驱动
     */
    public String driver;

    /**
     * 数据库连接URL
     */
    public String URL;

    /**
     * 数据库用户名
     */
    public String user;

    /**
     * 数据库用户密码
     */
    public String password;

    /**
     * 数据库最大连接数
     */
    public int maxConnection;

    /**
     * 数据库SQL查询每次连接返回执行到连接池，以确保它仍然是有效的。
     */
    public String validationQuery;

    private boolean validateOnCheckout;

    private int idleConnectionValidationSeconds;

    public String maxCachedStatementsPerConnection;

    private String discardIdleConnectionsSeconds;

    public static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

    public static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;

    /**
     * Druid连接池
     */
    private DruidDataSource datasource;

    /**
     * 获取数据库连接
     * @return 数据库连接
     * @throws SQLException 如果无法获取连接
     */
    @Override
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    /**
     * 关闭数据库连接
     * @throws SQLException 如果无法关闭连接
     */
    @Override
    public void shutdown() throws SQLException {
        datasource.close();
    }

    /**
     * 初始化数据库连接
     * @throws SQLException 如果无法初始化连接
     */
    @Override
    public void initialize() throws SQLException {
        if (this.URL == null) {
            throw new SQLException("DBPool could not be created: DB URL cannot be null");
        }

        if (this.driver == null) {
            throw new SQLException("DBPool driver could not be created: DB driver class name cannot be null!");
        }

        if (this.maxConnection < 0) {
            throw new SQLException("DBPool maxConnectins could not be created: Max connections must be greater than zero!");
        }

        datasource = new DruidDataSource();
        try{
            datasource.setDriverClassName(this.driver);
        } catch (Exception e) {
            try {
                throw new SchedulerException("Problem setting driver class name on datasource: " + e.getMessage(), e);
            } catch (SchedulerException e1) {
            }
        }

        datasource.setUrl(this.URL);
        datasource.setUsername(this.user);
        datasource.setPassword(this.password);
        datasource.setMaxActive(this.maxConnection);
        datasource.setMinIdle(1);
        datasource.setMaxWait(0);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(DEFAULT_DB_MAX_CONNECTIONS);

        if (this.validationQuery != null) {
            datasource.setValidationQuery(this.validationQuery);
            if(!this.validateOnCheckout)
                datasource.setTestOnReturn(true);
            else
                datasource.setTestOnBorrow(true);
            datasource.setValidationQueryTimeout(this.idleConnectionValidationSeconds);
        }
    }

}