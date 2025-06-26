//package com.xinyirun.scm.common.datasource;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import com.alibaba.druid.util.Utils;
//import com.xinyirun.scm.common.datasource.properties.DruidProperties;
//import com.xinyirun.scm.common.enums.datasource.DataSourceTypeEnum;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * druid 配置多数据源
// *
// * @author
// */
//@Configuration
//public class DruidConfig {
//
//    @Value("${spring.datasource.druid.master.url}")
//    private String url;
//
//    @Value("${spring.datasource.druid.master.username}")
//    private String username;
//
//    @Value("${spring.datasource.druid.master.password}")
//    private String password;
//
//    @Value("${spring.datasource.driverClassName}")
//    private String driverClassName;
//
//    private Map<String, Object> getProperties() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("driverClassName", driverClassName);
//        map.put("url", url);
//        map.put("username", username);
//        map.put("password", password);
//        return map;
//    }
//
////    @Bean("master")
////    @ConfigurationProperties(prefix ="spring.datasource.druid.master")
////    public DataSource masterDataSource(DruidProperties druidProperties) throws SQLException {
////        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
////        return druidProperties.dataSource(dataSource);
////    }
//
//    @Bean("dynamicDataSource")
//    public DataSource dynamicDataSource() {
//        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
//        Map<Object, Object> dataSourceMap = new HashMap<>(1);
//        dataSourceMap.put("default_db", dynamicRoutingDataSource.dataSource(getProperties()));
//        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
//        dynamicRoutingDataSource.setDefaultTargetDataSource(dynamicRoutingDataSource.dataSource(getProperties()));
//        return dynamicRoutingDataSource;
//    }
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource);
//        return sessionFactory.getObject();
//    }
//
////    @Bean(name = "dynamicDataSource")
////    @Primary
////    public DynamicDataSource dataSource(DataSource masterDataSource, DataSource slaveDataSource) {
////        Map<Object, Object> targetDataSources = new HashMap<>();
////        targetDataSources.put(DataSourceTypeEnum.master.name(), masterDataSource);
////        return new DynamicDataSource(masterDataSource, targetDataSources);
////    }
////
////    /**
////     * 去除监控页面底部的广告
////     */
////    @SuppressWarnings({"rawtypes", "unchecked"})
////    @Bean
////    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties) {
////        // 获取web监控页面的参数
////        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
////        // 提取common.js的配置路径
////        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
////        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
////        final String filePath = "support/http/resources/js/common.js";
////        // 创建filter进行过滤
////        Filter filter = new Filter() {
////            @Override
////            public void init(FilterConfig filterConfig) throws ServletException {}
////
////            @Override
////            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
////                throws IOException, ServletException {
////                chain.doFilter(request, response);
////                // 重置缓冲区，响应头不会被重置
////                response.resetBuffer();
////                // 获取common.js
////                String text = Utils.readFromResource(filePath);
////                // 正则替换banner, 除去底部的广告信息
////                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
////                text = text.replaceAll("powered.*?shrek.wang</a>", "");
////                response.getWriter().write(text);
////            }
////
////            @Override
////            public void destroy() {}
////        };
////        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
////        registrationBean.setFilter(filter);
////        registrationBean.addUrlPatterns(commonJsPattern);
////        return registrationBean;
////    }
////
////    @Bean
////    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
////        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<StatViewServlet>(
////                new StatViewServlet(), "/druid/*");
////        servletRegistrationBean.addInitParameter("allow", "127.0.0.1"); // 设置ip白名单
////        // 设置控制台管理用户
////        servletRegistrationBean.addInitParameter("loginUsername", "51e30c74");
////        servletRegistrationBean.addInitParameter("loginPassword", "4979b24a-46c0-11ec-81d3-0242ac130003");
////        // 是否可以重置数据
////        servletRegistrationBean.addInitParameter("resetEnable", "false");
////        return servletRegistrationBean;
////    }
////
////    @Bean
////    public FilterRegistrationBean<WebStatFilter> statFilter() {
////        // 创建过滤器
////        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<WebStatFilter>(
////                new WebStatFilter());
////        // 设置过滤器过滤路径
////        filterRegistrationBean.addUrlPatterns("/*");
////        // 忽略过滤的形式
////        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
////        return filterRegistrationBean;
////    }
//}
