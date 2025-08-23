package com.xinyirun.scm.report.controller;

import com.xinyirun.scm.report.entity.JimuReport;
import com.xinyirun.scm.report.service.JimuReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 积木报表FreeMarker视图控制器
 * 用于报表预览和打印页面渲染
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Slf4j
@Controller
@RequestMapping("/jmreport")
@RequiredArgsConstructor
public class JimuReportViewController {
    
    private final JimuReportService jimuReportService;
    
    /**
     * 报表工作台首页
     */
    @GetMapping("/list")
    public String reportList(Model model) {
        log.info("进入报表工作台首页");
        
        // 获取报表列表
        var reportList = jimuReportService.getReportList(null);
        model.addAttribute("reportList", reportList);
        
        return "jmreport/list";
    }
    
    /**
     * 报表设计器页面
     */
    @GetMapping("/index/{id}")
    public String reportDesigner(@PathVariable String id, Model model) {
        log.info("进入报表设计器，reportId: {}", id);
        
        // 获取报表信息
        JimuReport report = jimuReportService.getReportById(id);
        if (report == null) {
            model.addAttribute("errorMessage", "报表不存在: " + id);
            return "error/404";
        }
        
        model.addAttribute("report", report);
        model.addAttribute("reportId", id);
        model.addAttribute("reportCode", report.getCode());
        model.addAttribute("reportName", report.getName());
        model.addAttribute("jsonStr", report.getJsonStr() != null ? report.getJsonStr() : "{}");
        
        return "jmreport/designer";
    }
    
    /**
     * 报表预览页面
     */
    @GetMapping("/view/{id}")
    public String reportView(@PathVariable String id, 
                           @RequestParam(required = false) Map<String, String> params,
                           Model model) {
        log.info("进入报表预览页面，reportId: {}, params: {}", id, params);
        
        try {
            // 获取报表信息
            JimuReport report = jimuReportService.getReportById(id);
            if (report == null) {
                model.addAttribute("errorMessage", "报表不存在: " + id);
                return "error/404";
            }
            
            // 检查报表状态
            if (!"1".equals(report.getStatus())) {
                model.addAttribute("errorMessage", "报表未发布，无法预览");
                return "error/forbidden";
            }
            
            // 处理参数
            Map<String, Object> reportParams = new HashMap<>();
            if (params != null) {
                reportParams.putAll(params);
            }
            
            // 增加浏览次数
            jimuReportService.increaseViewCount(id);
            
            // 设置模板变量
            model.addAttribute("report", report);
            model.addAttribute("reportId", id);
            model.addAttribute("reportCode", report.getCode());
            model.addAttribute("reportName", report.getName());
            model.addAttribute("jsonStr", report.getJsonStr() != null ? report.getJsonStr() : "{}");
            model.addAttribute("params", reportParams);
            model.addAttribute("apiUrl", report.getApiUrl() != null ? report.getApiUrl() : "");
            model.addAttribute("cssStr", report.getCssStr() != null ? report.getCssStr() : "");
            model.addAttribute("jsStr", report.getJsStr() != null ? report.getJsStr() : "");
            
            return "jmreport/view";
            
        } catch (Exception e) {
            log.error("报表预览失败", e);
            model.addAttribute("errorMessage", "报表预览失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 报表打印页面
     */
    @GetMapping("/print/{id}")
    public String reportPrint(@PathVariable String id, 
                            @RequestParam(required = false) Map<String, String> params,
                            Model model) {
        log.info("进入报表打印页面，reportId: {}, params: {}", id, params);
        
        try {
            // 获取报表信息
            JimuReport report = jimuReportService.getReportById(id);
            if (report == null) {
                model.addAttribute("errorMessage", "报表不存在: " + id);
                return "error/404";
            }
            
            // 检查报表状态
            if (!"1".equals(report.getStatus())) {
                model.addAttribute("errorMessage", "报表未发布，无法打印");
                return "error/forbidden";
            }
            
            // 处理参数
            Map<String, Object> reportParams = new HashMap<>();
            if (params != null) {
                reportParams.putAll(params);
            }
            
            // 设置模板变量
            model.addAttribute("report", report);
            model.addAttribute("reportId", id);
            model.addAttribute("reportCode", report.getCode());
            model.addAttribute("reportName", report.getName());
            model.addAttribute("jsonStr", report.getJsonStr() != null ? report.getJsonStr() : "{}");
            model.addAttribute("params", reportParams);
            model.addAttribute("cssStr", report.getCssStr() != null ? report.getCssStr() : "");
            model.addAttribute("jsStr", report.getJsStr() != null ? report.getJsStr() : "");
            
            return "jmreport/print";
            
        } catch (Exception e) {
            log.error("报表打印失败", e);
            model.addAttribute("errorMessage", "报表打印失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 根据编码预览报表
     */
    @GetMapping("/viewByCode/{code}")
    public String reportViewByCode(@PathVariable String code,
                                 @RequestParam(required = false) Map<String, String> params,
                                 Model model) {
        log.info("根据编码预览报表，code: {}, params: {}", code, params);
        
        try {
            // 获取报表信息
            JimuReport report = jimuReportService.getReportByCode(code);
            if (report == null) {
                model.addAttribute("errorMessage", "报表不存在: " + code);
                return "error/404";
            }
            
            // 重定向到ID方式预览
            return "redirect:/jmreport/view/" + report.getId() + 
                   (params != null && !params.isEmpty() ? "?" + buildQueryString(params) : "");
            
        } catch (Exception e) {
            log.error("根据编码预览报表失败", e);
            model.addAttribute("errorMessage", "报表预览失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 报表配置页面
     */
    @GetMapping("/config/{id}")
    public String reportConfig(@PathVariable String id, Model model) {
        log.info("进入报表配置页面，reportId: {}", id);
        
        try {
            // 获取报表信息
            JimuReport report = jimuReportService.getReportById(id);
            if (report == null) {
                model.addAttribute("errorMessage", "报表不存在: " + id);
                return "error/404";
            }
            
            model.addAttribute("report", report);
            model.addAttribute("reportId", id);
            
            return "jmreport/config";
            
        } catch (Exception e) {
            log.error("报表配置失败", e);
            model.addAttribute("errorMessage", "报表配置失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 构建查询字符串
     */
    private String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        });
        return sb.toString();
    }
}