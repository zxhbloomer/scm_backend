package com.xinyirun.scm.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.report.entity.JimuReportDataSource;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 积木报表数据源Mapper接口
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Mapper
public interface JimuReportDataSourceMapper extends BaseMapper<JimuReportDataSource> {
    
    /**
     * 根据数据源编码查询数据源信息
     */
    @Select("""
            SELECT * FROM jimu_report_data_source 
            WHERE code = #{code} AND del_flag = 0
            """)
    JimuReportDataSource getByCode(@Param("code") String code);
    
    /**
     * 查询所有可用数据源列表
     */
    @Select("""
            SELECT id, code, name, remark, db_type, db_driver, 
                   create_by, create_time, update_by, update_time
            FROM jimu_report_data_source 
            WHERE del_flag = 0 
            ORDER BY create_time DESC
            """)
    List<JimuReportDataSource> getDataSourceList();
    
    /**
     * 根据数据库类型查询数据源
     */
    @Select("""
            SELECT * FROM jimu_report_data_source 
            WHERE db_type = #{dbType} AND del_flag = 0
            ORDER BY create_time DESC
            """)
    List<JimuReportDataSource> getByDbType(@Param("dbType") String dbType);
    
    /**
     * 根据名称模糊查询数据源
     */
    @Select("""
            SELECT * FROM jimu_report_data_source 
            WHERE name LIKE CONCAT('%', #{name}, '%') 
            AND del_flag = 0
            ORDER BY create_time DESC
            """)
    List<JimuReportDataSource> getByNameLike(@Param("name") String name);
}