package com.xinyirun.scm.excel.merge;

import com.xinyirun.scm.bean.system.vo.business.project.BProjectExportVo;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller使用示例
 * 
 * 演示如何在实际的Spring Boot Controller中使用新的简化合并策略
 * 替换原有的复杂预计算方法，实现项目管理Excel导出的单元格合并功能
 * 
 * @author SCM系统
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public class ControllerUsageExample {
    
    /**
     * 示例1：基本用法 - 项目管理全部导出
     * 
     * 替换原有的BProjectController.exportall方法实现
     */
    public void exportAllExample(List<BProjectExportVo> exportDataList, HttpServletResponse response) throws IOException {
        log.info("开始导出项目管理数据，记录数: {}", exportDataList.size());
        
        // 创建项目合并策略
        ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(false); // 生产环境关闭调试
        
        // 使用新的简化合并方法
        EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
        excelUtil.exportExcelWithMergeStrategy(
            "项目管理导出", 
            "项目列表", 
            exportDataList, 
            response, 
            mergeStrategy
        );
        
        log.info("项目管理数据导出完成");
    }
    
    /**
     * 示例2：链式调用 - 更简洁的写法
     * 
     * 适用于简单的导出场景
     */
    public void exportWithChainCallExample(List<BProjectExportVo> exportDataList, HttpServletResponse response) throws IOException {
        new EasyExcelUtil<>(BProjectExportVo.class)
            .withMergeStrategy(new ProjectMergeStrategy())
            .exportExcel("项目导出", "项目数据", exportDataList, response);
    }
    
    /**
     * 示例3：调试模式 - 开发环境使用
     * 
     * 启用详细日志，便于调试和验证合并效果
     */
    public void exportWithDebugExample(List<BProjectExportVo> exportDataList, HttpServletResponse response) throws IOException {
        // 开启调试模式，输出详细合并日志
        ProjectMergeStrategy debugMergeStrategy = new ProjectMergeStrategy(true);
        log.info("合并策略信息: {}", debugMergeStrategy.getStrategyInfo());
        
        EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
        excelUtil.exportExcelWithMergeStrategy(
            "项目管理调试导出", 
            "调试数据", 
            exportDataList, 
            response, 
            debugMergeStrategy
        );
    }
    
    /**
     * 示例4：条件合并 - 根据数据情况决定是否启用合并
     * 
     * 智能判断是否需要合并（当存在相同项目的多条记录时）
     */
    public void exportWithConditionalMergeExample(List<BProjectExportVo> exportDataList, HttpServletResponse response) throws IOException {
        // 检查是否有重复项目（需要合并）
        boolean needMerge = exportDataList.stream()
                .collect(java.util.stream.Collectors.groupingBy(BProjectExportVo::getCode))
                .values()
                .stream()
                .anyMatch(list -> list.size() > 1);
        
        EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
        
        if (needMerge) {
            log.info("检测到重复项目，启用合并策略");
            excelUtil.withMergeStrategy(new ProjectMergeStrategy());
        } else {
            log.info("无重复项目，使用标准导出");
        }
        
        excelUtil.exportExcel("智能项目导出", "项目列表", exportDataList, response);
    }
    
    /**
     * 示例5：性能监控 - 记录导出性能指标
     * 
     * 用于监控大数据量导出的性能表现
     */
    public void exportWithPerformanceMonitoringExample(List<BProjectExportVo> exportDataList, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        
        try {
            ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(false);
            
            EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
            excelUtil.exportExcelWithMergeStrategy(
                "项目管理性能测试", 
                "性能数据", 
                exportDataList, 
                response, 
                mergeStrategy
            );
            
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            double recordsPerSecond = exportDataList.size() * 1000.0 / duration;
            
            log.info("导出性能统计: 记录数={}, 耗时={}ms, 速度={:.2f}记录/秒", 
                    exportDataList.size(), duration, recordsPerSecond);
            
            // 可以发送到监控系统
            // metricsService.recordExportMetrics(exportDataList.size(), duration);
        }
    }
    
    /**
     * 示例6：异常处理 - 完整的错误处理机制
     * 
     * 生产环境推荐的完整实现
     */
    public void exportWithFullErrorHandlingExample(List<BProjectExportVo> exportDataList, HttpServletResponse response) {
        try {
            // 数据校验
            if (exportDataList == null || exportDataList.isEmpty()) {
                log.warn("导出数据为空，跳过导出操作");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }
            
            // 数据排序（确保合并正确）
            exportDataList.sort((a, b) -> {
                if (a.getCode() == null && b.getCode() == null) return 0;
                if (a.getCode() == null) return 1;
                if (b.getCode() == null) return -1;
                return a.getCode().compareTo(b.getCode());
            });
            
            log.info("开始导出项目数据: {} 条记录", exportDataList.size());
            
            ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(false);
            EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
            
            excelUtil.exportExcelWithMergeStrategy(
                "项目管理导出_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")), 
                "项目列表", 
                exportDataList, 
                response, 
                mergeStrategy
            );
            
            log.info("项目数据导出成功");
            
        } catch (IOException e) {
            log.error("导出Excel文件时发生IO异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("导出过程中发生未知异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}