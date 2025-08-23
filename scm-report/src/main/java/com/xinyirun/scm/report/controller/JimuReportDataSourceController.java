package com.xinyirun.scm.report.controller;

import com.xinyirun.scm.report.entity.JimuReportDataSource;
import com.xinyirun.scm.report.service.JimuReportDataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 积木报表数据源API控制器
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Slf4j
@RestController
@RequestMapping("/scm/report/datasource")
@RequiredArgsConstructor
public class JimuReportDataSourceController {
    
    private final JimuReportDataSourceService dataSourceService;
    
    /**
     * 获取数据源详情
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<JimuReportDataSource> getDataSourceDetail(@PathVariable String id) {
        log.info("获取数据源详情，dataSourceId: {}", id);
        
        JimuReportDataSource dataSource = dataSourceService.getById(id);
        if (dataSource == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dataSource);
    }
    
    /**
     * 根据编码获取数据源
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<JimuReportDataSource> getDataSourceByCode(@PathVariable String code) {
        log.info("根据编码获取数据源，code: {}", code);
        
        JimuReportDataSource dataSource = dataSourceService.getDataSourceByCode(code);
        if (dataSource == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dataSource);
    }
    
    /**
     * 查询数据源列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<JimuReportDataSource>> getDataSourceList() {
        log.info("查询数据源列表");
        
        List<JimuReportDataSource> dataSourceList = dataSourceService.getDataSourceList();
        return ResponseEntity.ok(dataSourceList);
    }
    
    /**
     * 根据数据库类型查询数据源
     */
    @GetMapping("/listByType/{dbType}")
    public ResponseEntity<List<JimuReportDataSource>> getDataSourceByType(@PathVariable String dbType) {
        log.info("根据数据库类型查询数据源，dbType: {}", dbType);
        
        List<JimuReportDataSource> dataSourceList = dataSourceService.getDataSourceByType(dbType);
        return ResponseEntity.ok(dataSourceList);
    }
    
    /**
     * 根据名称模糊查询数据源
     */
    @GetMapping("/listByName")
    public ResponseEntity<List<JimuReportDataSource>> getDataSourceByName(@RequestParam String name) {
        log.info("根据名称模糊查询数据源，name: {}", name);
        
        List<JimuReportDataSource> dataSourceList = dataSourceService.getDataSourceByName(name);
        return ResponseEntity.ok(dataSourceList);
    }
    
    /**
     * 保存数据源
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveDataSource(@RequestBody JimuReportDataSource dataSource) {
        log.info("保存数据源，数据源名称: {}", dataSource != null ? dataSource.getName() : "null");
        
        try {
            boolean success = dataSourceService.saveDataSource(dataSource);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "保存成功" : "保存失败"
            ));
        } catch (Exception e) {
            log.error("保存数据源失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "保存失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 更新数据源
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateDataSource(@RequestBody JimuReportDataSource dataSource) {
        log.info("更新数据源，dataSourceId: {}", dataSource != null ? dataSource.getId() : "null");
        
        try {
            boolean success = dataSourceService.updateDataSource(dataSource);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "更新成功" : "更新失败"
            ));
        } catch (Exception e) {
            log.error("更新数据源失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 删除数据源
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteDataSource(@PathVariable String id) {
        log.info("删除数据源，dataSourceId: {}", id);
        
        try {
            boolean success = dataSourceService.deleteDataSource(id);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "删除成功" : "删除失败"
            ));
        } catch (Exception e) {
            log.error("删除数据源失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 测试数据源连接
     */
    @PostMapping("/testConnection")
    public ResponseEntity<Map<String, Object>> testConnection(@RequestBody JimuReportDataSource dataSource) {
        log.info("测试数据源连接，数据源名称: {}", dataSource != null ? dataSource.getName() : "null");
        
        try {
            boolean success = dataSourceService.testConnection(dataSource);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "连接测试成功" : "连接测试失败"
            ));
        } catch (Exception e) {
            log.error("测试数据源连接失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "连接测试失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 执行SQL查询
     */
    @PostMapping("/executeQuery")
    public ResponseEntity<List<Map<String, Object>>> executeQuery(
            @RequestParam String dataSourceCode,
            @RequestParam String sql,
            @RequestBody(required = false) Map<String, Object> params) {
        log.info("执行SQL查询，dataSourceCode: {}, sql: {}", dataSourceCode, sql);
        
        try {
            List<Map<String, Object>> result = dataSourceService.executeQuery(dataSourceCode, sql, params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行SQL查询失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取数据库表名列表
     */
    @GetMapping("/tables/{dataSourceCode}")
    public ResponseEntity<List<String>> getTableNames(@PathVariable String dataSourceCode) {
        log.info("获取数据库表名列表，dataSourceCode: {}", dataSourceCode);
        
        try {
            List<String> tableNames = dataSourceService.getTableNames(dataSourceCode);
            return ResponseEntity.ok(tableNames);
        } catch (Exception e) {
            log.error("获取数据库表名列表失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取表结构信息
     */
    @GetMapping("/columns/{dataSourceCode}/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> getTableColumns(
            @PathVariable String dataSourceCode,
            @PathVariable String tableName) {
        log.info("获取表结构信息，dataSourceCode: {}, tableName: {}", dataSourceCode, tableName);
        
        try {
            List<Map<String, Object>> columns = dataSourceService.getTableColumns(dataSourceCode, tableName);
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            log.error("获取表结构信息失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}