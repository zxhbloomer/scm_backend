package com.xinyirun.scm.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.report.entity.JimuReportDataSource;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 积木报表数据源业务接口
 *
 * @author SCM Team
 * @since 2025-01-22
 */
public interface JimuReportDataSourceService extends IService<JimuReportDataSource> {
    
    /**
     * 根据数据源编码获取数据源信息
     * 
     * @param code 数据源编码
     * @return 数据源信息
     */
    JimuReportDataSource getDataSourceByCode(String code);
    
    /**
     * 获取数据源列表
     *
     * @return 数据源列表
     */
    List<JimuReportDataSource> getDataSourceList();
    
    /**
     * 根据数据库类型获取数据源列表
     *
     * @param dbType 数据库类型
     * @return 数据源列表
     */
    List<JimuReportDataSource> getDataSourceByType(String dbType);
    
    /**
     * 根据名称模糊查询数据源
     *
     * @param name 数据源名称
     * @return 数据源列表
     */
    List<JimuReportDataSource> getDataSourceByName(String name);
    
    /**
     * 保存数据源
     *
     * @param dataSource 数据源信息
     * @return 保存结果
     */
    boolean saveDataSource(JimuReportDataSource dataSource);
    
    /**
     * 更新数据源
     *
     * @param dataSource 数据源信息
     * @return 更新结果
     */
    boolean updateDataSource(JimuReportDataSource dataSource);
    
    /**
     * 删除数据源
     *
     * @param dataSourceId 数据源ID
     * @return 删除结果
     */
    boolean deleteDataSource(String dataSourceId);
    
    /**
     * 测试数据源连接
     *
     * @param dataSource 数据源信息
     * @return 测试结果
     */
    boolean testConnection(JimuReportDataSource dataSource);
    
    /**
     * 获取数据源连接
     *
     * @param dataSourceCode 数据源编码
     * @return 数据库连接
     */
    Connection getConnection(String dataSourceCode);
    
    /**
     * 执行SQL查询
     *
     * @param dataSourceCode 数据源编码
     * @param sql SQL语句
     * @param params 查询参数
     * @return 查询结果
     */
    List<Map<String, Object>> executeQuery(String dataSourceCode, String sql, Map<String, Object> params);
    
    /**
     * 获取表结构信息
     *
     * @param dataSourceCode 数据源编码
     * @param tableName 表名
     * @return 表结构信息
     */
    List<Map<String, Object>> getTableColumns(String dataSourceCode, String tableName);
    
    /**
     * 获取数据库中所有表名
     *
     * @param dataSourceCode 数据源编码
     * @return 表名列表
     */
    List<String> getTableNames(String dataSourceCode);
}