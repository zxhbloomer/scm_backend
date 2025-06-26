package com.xinyirun.scm.common.datasource.properties;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

/**
 * druid 配置属性
 *
 * @author
 */
@Configuration
public class DruidProperties implements Serializable {

    private static final long serialVersionUID = -8374942539718940542L;

    @Value("${spring.datasource.druid.initial-size}")
    private int initialSize;

    @Value("${spring.datasource.druid.max-active}")
    private int maxActive;

    @Value("${spring.datasource.druid.min-idle}")
    private int minIdle;

    @Value("${spring.datasource.druid.max-wait}")
    private int maxWait;

    @Value("${spring.datasource.druid.keep-alive}")
    private Boolean keepAlive;

    @Value("${spring.datasource.druid.min-evictable-idle-time-millis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.max-evictable-idle-time-millis}")
    private int maxEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.time-between-eviction-runs-millis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.validation-query}")
    private String validationQuery;

    @Value("${spring.datasource.druid.validation-query-timeout}")
    private int validationQueryTimeout;

    @Value("${spring.datasource.druid.test-on-borrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.test-on-return}")
    private boolean testOnReturn;

    @Value("${spring.datasource.druid.test-while-idle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.max-pool-prepared-statement-per-connection-size}")
    private int maxPoolPreparedStatementPerConnectionSize ;

    @Value("${spring.datasource.druid.pool-prepared-statements}")
    private boolean poolPreparedStatements ;

    @Value("${spring.datasource.druid.max-open-prepared-statements}")
    private int maxOpenPreparedStatements ;

    @Value("${spring.datasource.druid.filters}")
    private String  filters ;

    @Value("${spring.datasource.druid.share-prepared-statements}")
    private boolean sharePreparedStatements ;

//    @Value("${spring.datasource.druid.connect-properties}")
//    private Properties connectProperties ;

    public DruidDataSource dataSource(DruidDataSource datasource) throws SQLException {
        /** 配置初始化大小、最小、最大 */
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);

        /** 配置获取连接等待超时的时间 */
        datasource.setMaxWait(maxWait);

        datasource.setKeepAlive(keepAlive);

        /** 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        /** 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);

        /**
         * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。
         * 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
         */
        datasource.setValidationQuery(validationQuery);

        datasource.setValidationQueryTimeout(validationQueryTimeout);

        /** 建议配置为true，不影响性能，并且保证安全性。
         * 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
        datasource.setTestWhileIdle(testWhileIdle);

        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);

        datasource.setPoolPreparedStatements(poolPreparedStatements);

        /** 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnBorrow(testOnBorrow);

        /** 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnReturn(testOnReturn);

        datasource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);

        datasource.setFilters(filters);

        datasource.setSharePreparedStatements(sharePreparedStatements);

//        datasource.setConnectProperties(connectProperties);
        return datasource;
    }
}