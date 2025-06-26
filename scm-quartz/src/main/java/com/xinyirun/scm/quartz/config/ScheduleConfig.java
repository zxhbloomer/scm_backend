package com.xinyirun.scm.quartz.config;

import com.xinyirun.scm.quartz.config.listener.SystemJobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 定时任务配置
 *
 * @author Administrator
 */
//@Configuration
//@ComponentScan({"com.xinyirun.scm.common"})
@Configuration
public class ScheduleConfig {


    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Order(1)
    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
        return schedulerFactoryBean -> {
            schedulerFactoryBean.setDataSource(dataSource);
            schedulerFactoryBean.setTransactionManager(new DataSourceTransactionManager(dataSource));
        };
    }
//    @Autowired
//    @Qualifier("master")
//    DataSource dataSource;
//
//    @Value("${spring.datasource.druid.master.url}")
//    private String url;
//
//    @Value("${spring.datasource.druid.master.username}")
//    private String user;
//
//    @Value("${spring.datasource.druid.master.password}")
//    private String password;
//
//    @Value("${spring.datasource.druid.master.driverClassName}")
//    private String driver;
//
//    @Autowired
//    private SystemJobListener jobCompletionListener;

//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setDataSource(dataSource);
//
//        // quartz参数
//        Properties prop = new Properties();
//        prop.put("org.quartz.scheduler.instanceName", "WmsScheduler");
//        prop.put("org.quartz.scheduler.instanceId", "AUTO");
//        // 线程池配置
//        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
//        prop.put("org.quartz.threadPool.threadCount", "1");
//        prop.put("org.quartz.threadPool.threadPriority", "5");
//
//        // 集群配置
//        prop.put("org.quartz.jobStore.isClustered", "true");
//        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
//        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
//        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
//        prop.put("org.quartz.jobStore.acquireTriggersWithinLock", "true");
//
//        // sqlserver 启用
//        // prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");
//        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
//        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
//
//        // JobStore配置
////        prop.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
//        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
//
//        // 使用JobStoreTX后需要配置quartz连接池
//        prop.put("org.quartz.jobStore.useProperties", "false");
//        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
//        prop.put("org.quartz.jobStore.dataSource", "wmsQuartzDS");
//        prop.put("org.quartz.dataSource.wmsQuartzDS.connectionProvider.class", "com.xinyirun.scm.quartz.config.DruidConnectionProvider");
//        prop.put("org.quartz.dataSource.wmsQuartzDS.driver", "com.mysql.cj.jdbc.Driver");
//        prop.put("org.quartz.dataSource.wmsQuartzDS.URL", url);
//        prop.put("org.quartz.dataSource.wmsQuartzDS.user", user);
//        prop.put("org.quartz.dataSource.wmsQuartzDS.password", password);
//        prop.put("org.quartz.dataSource.wmsQuartzDS.maxConnection", "5");
//
//        factory.setQuartzProperties(prop);
//
//        factory.setSchedulerName("WmsScheduler");
//        // 延时启动
//        factory.setStartupDelay(1);
//        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
//        // 可选，QuartzScheduler
//        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
//        factory.setOverwriteExistingJobs(true);
//        // 设置自动启动，默认为true
//        factory.setAutoStartup(true);
//
//        // 添加监听器
//        factory.setGlobalJobListeners(jobCompletionListener);
//
//        return factory;
//    }
}