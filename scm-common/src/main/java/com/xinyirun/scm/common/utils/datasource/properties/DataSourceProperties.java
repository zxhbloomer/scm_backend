package com.xinyirun.scm.common.utils.datasource.properties;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @note
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties extends DataSourceProperty {

    /**
     * 查询数据源的 sql，优先级高于 dsName
     */
    private String tenent_sql;
}