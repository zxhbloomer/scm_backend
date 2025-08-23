package com.xinyirun.scm.report.controller;

import com.xinyirun.scm.report.entity.JimuReport;
import com.xinyirun.scm.report.service.JimuReportService;
import com.xinyirun.scm.report.vo.ReportQueryVo;
import com.xinyirun.scm.report.vo.ReportResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 积木报表API控制器
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Slf4j
@RestController
@RequestMapping("/scm/report")
@RequiredArgsConstructor
public class JimuReportController {
    
    private final JimuReportService jimuReportService;
    
    /**
     * 获取报表详情
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<JimuReport> getReportDetail(@PathVariable String id) {
        log.info("获取报表详情，reportId: {}", id);
        
        JimuReport report = jimuReportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(report);
    }
    
    /**
     * 根据编码获取报表
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<JimuReport> getReportByCode(@PathVariable String code) {
        log.info("根据编码获取报表，code: {}", code);
        
        JimuReport report = jimuReportService.getReportByCode(code);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(report);
    }
    
    /**
     * 查询报表列表
     */
    @PostMapping("/list")
    public ResponseEntity<List<JimuReport>> getReportList(@RequestBody(required = false) ReportQueryVo queryVo) {
        log.info("查询报表列表，查询条件: {}", queryVo);
        
        List<JimuReport> reportList = jimuReportService.getReportList(queryVo);
        return ResponseEntity.ok(reportList);
    }
    
    /**
     * 保存报表
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveReport(@RequestBody JimuReport report) {
        log.info("保存报表，报表名称: {}", report != null ? report.getName() : "null");
        
        try {
            boolean success = jimuReportService.saveReport(report);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "保存成功" : "保存失败"
            ));
        } catch (Exception e) {
            log.error("保存报表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "保存失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 更新报表
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateReport(@RequestBody JimuReport report) {
        log.info("更新报表，reportId: {}", report != null ? report.getId() : "null");
        
        try {
            boolean success = jimuReportService.updateReport(report);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "更新成功" : "更新失败"
            ));
        } catch (Exception e) {
            log.error("更新报表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 删除报表
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteReport(@PathVariable String id) {
        log.info("删除报表，reportId: {}", id);
        
        try {
            boolean success = jimuReportService.deleteReport(id);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "删除成功" : "删除失败"
            ));
        } catch (Exception e) {
            log.error("删除报表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 执行报表
     */
    @PostMapping("/execute/{id}")
    public ResponseEntity<ReportResultVo> executeReport(@PathVariable String id,
                                                        @RequestBody(required = false) Map<String, Object> params) {
        log.info("执行报表，reportId: {}, params: {}", id, params);
        
        try {
            ReportResultVo result = jimuReportService.executeReport(id, params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行报表失败", e);
            ReportResultVo errorResult = new ReportResultVo();
            errorResult.setStatus("error");
            errorResult.setErrorMessage("执行报表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
    
    /**
     * 预览报表
     */
    @PostMapping("/preview/{id}")
    public ResponseEntity<Map<String, Object>> previewReport(@PathVariable String id,
                                                             @RequestBody(required = false) Map<String, Object> params) {
        log.info("预览报表，reportId: {}, params: {}", id, params);
        
        try {
            Map<String, Object> result = jimuReportService.previewReport(id, params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("预览报表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "预览报表失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 导出报表
     */
    @PostMapping("/export/{id}")
    public ResponseEntity<byte[]> exportReport(@PathVariable String id,
                                               @RequestParam String exportType,
                                               @RequestBody(required = false) Map<String, Object> params) {
        log.info("导出报表，reportId: {}, exportType: {}", id, exportType);
        
        try {
            byte[] fileData = jimuReportService.exportReport(id, params, exportType);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            String fileName = "report_" + id + "." + exportType;
            
            if ("excel".equals(exportType)) {
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            } else if ("pdf".equals(exportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
            }
            
            headers.setContentDispositionFormData("attachment", fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);
                    
        } catch (Exception e) {
            log.error("导出报表失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 发布报表
     */
    @PostMapping("/publish/{id}")
    public ResponseEntity<Map<String, Object>> publishReport(@PathVariable String id) {
        log.info("发布报表，reportId: {}", id);
        
        try {
            boolean success = jimuReportService.publishReport(id);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "发布成功" : "发布失败"
            ));
        } catch (Exception e) {
            log.error("发布报表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "发布失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 取消发布报表
     */
    @PostMapping("/unpublish/{id}")
    public ResponseEntity<Map<String, Object>> unpublishReport(@PathVariable String id) {
        log.info("取消发布报表，reportId: {}", id);
        
        try {
            boolean success = jimuReportService.unpublishReport(id);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "取消发布成功" : "取消发布失败"
            ));
        } catch (Exception e) {
            log.error("取消发布报表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "取消发布失败: " + e.getMessage()
            ));
        }
    }
}