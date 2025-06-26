package com.xinyirun.scm.framework.config.datasource;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.xinyirun.scm.common.utils.datasource.properties.DataSourceProperties;
import com.xinyirun.scm.framework.config.datasource.provider.DynamicDataSourceProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kele-Bingtang
 * @date 2023/12/26 20:59
 * @note
 */
@AutoConfiguration
@EnableConfigurationProperties(DataSourceProperties.class)
@AutoConfigureAfter({DefaultDataSourceCreator.class})
public class DynamicDataSourceAutoConfiguration {

    private final DynamicDataSourceProperties properties;

    public DynamicDataSourceAutoConfiguration(DynamicDataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
                                                               DataSourceProperties dataSourceProperties,
                                                               DynamicDataSourceProperties dynamicDataSourceProperties) {
        // 获取动态配置主数据源
        String primary = dynamicDataSourceProperties.getPrimary();
        Map<String, DataSourceProperty> datasource = dynamicDataSourceProperties.getDatasource();
        DataSourceProperty primaryDataSourceProperty = datasource.get(primary);
        if (Objects.nonNull(primaryDataSourceProperty)) {
            BeanUtils.copyProperties(primaryDataSourceProperty, dataSourceProperties);
            // 动态配置主数据源存在，则不需要重新创建主数据源
            return new DynamicDataSourceProvider(defaultDataSourceCreator, dataSourceProperties, false);
        }

        return new DynamicDataSourceProvider(defaultDataSourceCreator, dataSourceProperties, true);
    }

    @Primary
    @Bean
    public DataSource dataSource(List<com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider> providers) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource(providers);
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }

}