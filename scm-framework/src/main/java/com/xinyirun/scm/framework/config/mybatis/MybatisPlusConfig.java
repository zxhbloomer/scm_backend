package com.xinyirun.scm.framework.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xinyirun.scm.core.system.config.mybatis.sqlinjector.SystemSqlInjector;
import com.xinyirun.scm.framework.config.mybatis.plugin.autofill.MyBatisAutoFillHandel;
import com.xinyirun.scm.framework.config.mybatis.plugin.datachange.DataChangeInterceptor;
import com.xinyirun.scm.framework.config.mybatis.plugin.datascope.DataScopeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author zxh
 */
@Component
@Slf4j
@Configuration
public class MybatisPlusConfig  {

    /**
     * mybatis-plus乐观锁，分页插件<br>
     * 文档：http://mp.baomidou.com<br>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 通用字段补全
     */
    @Bean
    public MetaObjectHandler commonFieldFillHandler() {
        return new MyBatisAutoFillHandel();
    }

    /**
     * 数据范围mybatis插件,数据权限
     */
    @Bean
    public DataScopeInterceptor dataScopeInterceptor() {
        return new DataScopeInterceptor();
    }

    /**
     * 数据变动记录插件
     */
    @Bean
    public DataChangeInterceptor dataChangeInterceptor() {
        return new DataChangeInterceptor();
    }
    /**
     * 自定义 SqlInjector
     * 里面包含自定义的全局方法
     */
    @Bean
    public SystemSqlInjector wmsSqlInjector() {
        return new SystemSqlInjector();
    }
}
