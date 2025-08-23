package com.xinyirun.scm.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.report.entity.JimuReport;
import com.xinyirun.scm.report.mapper.JimuReportMapper;
import com.xinyirun.scm.report.service.JimuReportService;
import com.xinyirun.scm.report.vo.ReportQueryVo;
import com.xinyirun.scm.report.vo.ReportResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 积木报表业务实现类
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class JimuReportServiceImpl extends ServiceImpl<JimuReportMapper, JimuReport> implements JimuReportService {

    private final JimuReportMapper jimuReportMapper;

    @Override
    public JimuReport getReportById(String reportId) {
        log.debug("查询报表详情，reportId: {}", reportId);
        if (reportId == null || reportId.trim().isEmpty()) {
            return null;
        }
        return jimuReportMapper.getFullById(reportId);
    }

    @Override
    public JimuReport getReportByCode(String code) {
        log.debug("根据编码查询报表，code: {}", code);
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return jimuReportMapper.getByCode(code);
    }

    @Override
    public List<JimuReport> getReportList(ReportQueryVo queryVo) {
        log.debug("查询报表列表，查询条件: {}", queryVo);
        
        if (queryVo == null) {
            return jimuReportMapper.getReportList();
        }
        
        // 使用MyBatis Plus条件构建器安全构建查询条件
        var queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<JimuReport>();
        
        queryWrapper.eq("del_flag", 0);
        
        if (queryVo.getName() != null && !queryVo.getName().trim().isEmpty()) {
            queryWrapper.like("name", queryVo.getName());
        }
        
        if (queryVo.getCode() != null && !queryVo.getCode().trim().isEmpty()) {
            queryWrapper.like("code", queryVo.getCode());
        }
        
        if (queryVo.getStatus() != null && !queryVo.getStatus().trim().isEmpty()) {
            queryWrapper.eq("status", queryVo.getStatus());
        }
        
        if (queryVo.getType() != null && !queryVo.getType().trim().isEmpty()) {
            queryWrapper.eq("type", queryVo.getType());
        }
        
        if (queryVo.getCreateBy() != null && !queryVo.getCreateBy().trim().isEmpty()) {
            queryWrapper.eq("create_by", queryVo.getCreateBy());
        }
        
        queryWrapper.select("id", "code", "name", "note", "status", "type", "api_url", 
                           "thumb", "template", "view_count", "create_by", "create_time", 
                           "update_by", "update_time");
        queryWrapper.orderByDesc("update_time");
        
        return this.list(queryWrapper);
    }

    @Override
    public boolean saveReport(JimuReport report) {
        log.info("保存报表，报表信息: {}", report != null ? report.getName() : "null");
        
        if (report == null) {
            throw new IllegalArgumentException("报表信息不能为空");
        }
        
        // 检查编码是否重复
        if (report.getCode() != null) {
            JimuReport existing = jimuReportMapper.getByCode(report.getCode());
            if (existing != null) {
                throw new IllegalArgumentException("报表编码已存在: " + report.getCode());
            }
        }
        
        // 设置默认值
        if (report.getStatus() == null) {
            report.setStatus("0"); // 默认设计中
        }
        if (report.getTemplate() == null) {
            report.setTemplate(0); // 默认非模板
        }
        if (report.getViewCount() == null) {
            report.setViewCount(0L); // 默认浏览次数为0
        }
        
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        report.setDelFlag(0);
        
        return this.save(report);
    }

    @Override
    public boolean updateReport(JimuReport report) {
        log.info("更新报表，报表ID: {}", report != null ? report.getId() : "null");
        
        if (report == null || report.getId() == null) {
            throw new IllegalArgumentException("报表ID不能为空");
        }
        
        // 检查报表是否存在
        JimuReport existing = this.getById(report.getId());
        if (existing == null) {
            throw new IllegalArgumentException("报表不存在: " + report.getId());
        }
        
        // 如果修改了编码，检查新编码是否重复
        if (report.getCode() != null && !report.getCode().equals(existing.getCode())) {
            JimuReport codeExists = jimuReportMapper.getByCode(report.getCode());
            if (codeExists != null && !codeExists.getId().equals(report.getId())) {
                throw new IllegalArgumentException("报表编码已存在: " + report.getCode());
            }
        }
        
        report.setUpdateTime(LocalDateTime.now());
        return this.updateById(report);
    }

    @Override
    public boolean deleteReport(String reportId) {
        log.info("删除报表，reportId: {}", reportId);
        
        if (reportId == null || reportId.trim().isEmpty()) {
            throw new IllegalArgumentException("报表ID不能为空");
        }
        
        // 检查报表是否存在
        JimuReport existing = this.getById(reportId);
        if (existing == null) {
            throw new IllegalArgumentException("报表不存在: " + reportId);
        }
        
        // 逻辑删除
        return this.removeById(reportId);
    }

    @Override
    public ReportResultVo executeReport(String reportId, Map<String, Object> params) {
        log.info("执行报表，reportId: {}, params: {}", reportId, params);
        
        long startTime = System.currentTimeMillis();
        ReportResultVo result = new ReportResultVo();
        
        try {
            // 获取报表信息
            JimuReport report = this.getReportById(reportId);
            if (report == null) {
                result.setStatus("error");
                result.setErrorMessage("报表不存在: " + reportId);
                return result;
            }
            
            // 检查报表状态
            if (!"1".equals(report.getStatus())) {
                result.setStatus("error"); 
                result.setErrorMessage("报表未发布，无法执行");
                return result;
            }
            
            result.setReportId(reportId);
            result.setReportCode(report.getCode());
            result.setReportName(report.getName());
            result.setJsonStr(report.getJsonStr());
            result.setParams(params);
            
            // 增加浏览次数
            this.increaseViewCount(reportId);
            
            // TODO: 这里需要实现具体的报表数据查询逻辑
            // 根据报表配置的数据源和SQL，执行查询
            // 暂时返回空数据
            result.setStatus("success");
            result.setDataList(List.of());
            result.setTotal(0L);
            
            long endTime = System.currentTimeMillis();
            result.setExecuteTime(endTime - startTime);
            
            log.info("报表执行完成，耗时: {}ms", result.getExecuteTime());
            return result;
            
        } catch (Exception e) {
            log.error("报表执行失败", e);
            result.setStatus("error");
            result.setErrorMessage("报表执行失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> previewReport(String reportId, Map<String, Object> params) {
        log.info("预览报表，reportId: {}, params: {}", reportId, params);
        
        // 获取报表信息
        JimuReport report = this.getReportById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报表不存在: " + reportId);
        }
        
        // TODO: 实现报表预览逻辑
        // 返回报表预览所需的所有数据
        return Map.of(
            "report", report,
            "params", params != null ? params : Map.of()
        );
    }

    @Override
    public byte[] exportReport(String reportId, Map<String, Object> params, String exportType) {
        log.info("导出报表，reportId: {}, exportType: {}", reportId, exportType);
        
        if (!"excel".equals(exportType) && !"pdf".equals(exportType)) {
            throw new IllegalArgumentException("不支持的导出类型: " + exportType);
        }
        
        // 执行报表获取数据
        ReportResultVo reportResult = this.executeReport(reportId, params);
        if (!"success".equals(reportResult.getStatus())) {
            throw new RuntimeException("报表执行失败: " + reportResult.getErrorMessage());
        }
        
        // TODO: 实现具体的导出逻辑
        // 根据exportType使用POI或OpenPDF生成文件
        throw new UnsupportedOperationException("导出功能待实现");
    }

    @Override
    public void increaseViewCount(String reportId) {
        log.debug("增加报表浏览次数，reportId: {}", reportId);
        
        if (reportId != null && !reportId.trim().isEmpty()) {
            jimuReportMapper.updateViewCount(reportId);
        }
    }

    @Override
    public boolean publishReport(String reportId) {
        log.info("发布报表，reportId: {}", reportId);
        
        if (reportId == null || reportId.trim().isEmpty()) {
            throw new IllegalArgumentException("报表ID不能为空");
        }
        
        JimuReport report = this.getById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报表不存在: " + reportId);
        }
        
        report.setStatus("1"); // 设置为已发布
        report.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(report);
    }

    @Override
    public boolean unpublishReport(String reportId) {
        log.info("取消发布报表，reportId: {}", reportId);
        
        if (reportId == null || reportId.trim().isEmpty()) {
            throw new IllegalArgumentException("报表ID不能为空");
        }
        
        JimuReport report = this.getById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报表不存在: " + reportId);
        }
        
        report.setStatus("0"); // 设置为设计中
        report.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(report);
    }
}