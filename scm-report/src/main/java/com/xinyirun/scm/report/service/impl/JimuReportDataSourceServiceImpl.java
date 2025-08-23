package com.xinyirun.scm.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.report.entity.JimuReportDataSource;
import com.xinyirun.scm.report.mapper.JimuReportDataSourceMapper;
import com.xinyirun.scm.report.service.JimuReportDataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 积木报表数据源业务实现类
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class JimuReportDataSourceServiceImpl extends ServiceImpl<JimuReportDataSourceMapper, JimuReportDataSource> 
        implements JimuReportDataSourceService {

    private final JimuReportDataSourceMapper dataSourceMapper;
    
    // 数据源连接池缓存
    private final Map<String, Connection> connectionCache = new ConcurrentHashMap<>();

    @Override
    public JimuReportDataSource getDataSourceByCode(String code) {
        log.debug("根据编码查询数据源，code: {}", code);
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return dataSourceMapper.getByCode(code);
    }

    @Override
    public List<JimuReportDataSource> getDataSourceList() {
        log.debug("查询所有数据源列表");
        return dataSourceMapper.getDataSourceList();
    }

    @Override
    public List<JimuReportDataSource> getDataSourceByType(String dbType) {
        log.debug("根据类型查询数据源，dbType: {}", dbType);
        if (dbType == null || dbType.trim().isEmpty()) {
            return List.of();
        }
        return dataSourceMapper.getByDbType(dbType);
    }

    @Override
    public List<JimuReportDataSource> getDataSourceByName(String name) {
        log.debug("根据名称模糊查询数据源，name: {}", name);
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        return dataSourceMapper.getByNameLike(name);
    }

    @Override
    public boolean saveDataSource(JimuReportDataSource dataSource) {
        log.info("保存数据源，数据源信息: {}", dataSource != null ? dataSource.getName() : "null");
        
        if (dataSource == null) {
            throw new IllegalArgumentException("数据源信息不能为空");
        }
        
        // 检查编码是否重复
        if (dataSource.getCode() != null) {
            JimuReportDataSource existing = dataSourceMapper.getByCode(dataSource.getCode());
            if (existing != null) {
                throw new IllegalArgumentException("数据源编码已存在: " + dataSource.getCode());
            }
        }
        
        dataSource.setCreateTime(LocalDateTime.now());
        dataSource.setUpdateTime(LocalDateTime.now());
        dataSource.setDelFlag(0);
        
        return this.save(dataSource);
    }

    @Override
    public boolean updateDataSource(JimuReportDataSource dataSource) {
        log.info("更新数据源，数据源ID: {}", dataSource != null ? dataSource.getId() : "null");
        
        if (dataSource == null || dataSource.getId() == null) {
            throw new IllegalArgumentException("数据源ID不能为空");
        }
        
        // 检查数据源是否存在
        JimuReportDataSource existing = this.getById(dataSource.getId());
        if (existing == null) {
            throw new IllegalArgumentException("数据源不存在: " + dataSource.getId());
        }
        
        // 如果修改了编码，检查新编码是否重复
        if (dataSource.getCode() != null && !dataSource.getCode().equals(existing.getCode())) {
            JimuReportDataSource codeExists = dataSourceMapper.getByCode(dataSource.getCode());
            if (codeExists != null && !codeExists.getId().equals(dataSource.getId())) {
                throw new IllegalArgumentException("数据源编码已存在: " + dataSource.getCode());
            }
        }
        
        dataSource.setUpdateTime(LocalDateTime.now());
        
        // 清除缓存的连接
        connectionCache.remove(existing.getCode());
        
        return this.updateById(dataSource);
    }

    @Override
    public boolean deleteDataSource(String dataSourceId) {
        log.info("删除数据源，dataSourceId: {}", dataSourceId);
        
        if (dataSourceId == null || dataSourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源ID不能为空");
        }
        
        // 检查数据源是否存在
        JimuReportDataSource existing = this.getById(dataSourceId);
        if (existing == null) {
            throw new IllegalArgumentException("数据源不存在: " + dataSourceId);
        }
        
        // 清除缓存的连接
        connectionCache.remove(existing.getCode());
        
        // 逻辑删除
        return this.removeById(dataSourceId);
    }

    @Override
    public boolean testConnection(JimuReportDataSource dataSource) {
        log.info("测试数据源连接，数据源: {}", dataSource != null ? dataSource.getName() : "null");
        
        if (dataSource == null) {
            return false;
        }
        
        Connection connection = null;
        try {
            // 加载驱动
            Class.forName(dataSource.getDbDriver());
            
            // 创建连接
            connection = DriverManager.getConnection(
                dataSource.getDbUrl(),
                dataSource.getDbUsername(),
                dataSource.getDbPassword()
            );
            
            // 测试连接有效性
            return connection != null && !connection.isClosed();
            
        } catch (Exception e) {
            log.error("数据源连接测试失败", e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.warn("关闭测试连接失败", e);
                }
            }
        }
    }

    @Override
    public Connection getConnection(String dataSourceCode) {
        log.debug("获取数据源连接，dataSourceCode: {}", dataSourceCode);
        
        if (dataSourceCode == null || dataSourceCode.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源编码不能为空");
        }
        
        // 从缓存获取连接
        Connection cachedConnection = connectionCache.get(dataSourceCode);
        if (cachedConnection != null) {
            try {
                if (!cachedConnection.isClosed()) {
                    return cachedConnection;
                }
            } catch (SQLException e) {
                log.warn("检查缓存连接状态失败", e);
            }
            // 移除无效连接
            connectionCache.remove(dataSourceCode);
        }
        
        // 获取数据源配置
        JimuReportDataSource dataSource = dataSourceMapper.getByCode(dataSourceCode);
        if (dataSource == null) {
            throw new IllegalArgumentException("数据源不存在: " + dataSourceCode);
        }
        
        try {
            // 加载驱动
            Class.forName(dataSource.getDbDriver());
            
            // 创建新连接
            Connection connection = DriverManager.getConnection(
                dataSource.getDbUrl(),
                dataSource.getDbUsername(),
                dataSource.getDbPassword()
            );
            
            // 缓存连接
            connectionCache.put(dataSourceCode, connection);
            
            return connection;
            
        } catch (Exception e) {
            log.error("获取数据源连接失败", e);
            throw new RuntimeException("获取数据源连接失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> executeQuery(String dataSourceCode, String sql, Map<String, Object> params) {
        log.info("执行SQL查询，dataSourceCode: {}, sql: {}", dataSourceCode, sql);
        
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL语句不能为空");
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = this.getConnection(dataSourceCode);
            statement = connection.prepareStatement(sql);
            
            // 设置参数
            if (params != null && !params.isEmpty()) {
                int paramIndex = 1;
                for (Object value : params.values()) {
                    statement.setObject(paramIndex++, value);
                }
            }
            
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                result.add(row);
            }
            
            log.info("SQL查询完成，返回 {} 条记录", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("执行SQL查询失败", e);
            throw new RuntimeException("执行SQL查询失败: " + e.getMessage(), e);
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // 注意：这里不关闭connection，因为需要复用
            } catch (SQLException e) {
                log.warn("关闭数据库资源失败", e);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getTableColumns(String dataSourceCode, String tableName) {
        log.debug("获取表结构信息，dataSourceCode: {}, tableName: {}", dataSourceCode, tableName);
        
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        
        String sql = """
                SELECT 
                    COLUMN_NAME as columnName,
                    DATA_TYPE as dataType,
                    IS_NULLABLE as nullable,
                    COLUMN_DEFAULT as defaultValue,
                    COLUMN_COMMENT as comment
                FROM INFORMATION_SCHEMA.COLUMNS 
                WHERE TABLE_NAME = ?
                ORDER BY ORDINAL_POSITION
                """;
        
        Map<String, Object> params = Map.of("tableName", tableName);
        return this.executeQuery(dataSourceCode, sql, params);
    }

    @Override
    public List<String> getTableNames(String dataSourceCode) {
        log.debug("获取数据库所有表名，dataSourceCode: {}", dataSourceCode);
        
        String sql = """
                SELECT TABLE_NAME as tableName
                FROM INFORMATION_SCHEMA.TABLES 
                WHERE TABLE_SCHEMA = DATABASE()
                ORDER BY TABLE_NAME
                """;
        
        List<Map<String, Object>> result = this.executeQuery(dataSourceCode, sql, null);
        return result.stream()
                .map(row -> (String) row.get("tableName"))
                .toList();
    }
}