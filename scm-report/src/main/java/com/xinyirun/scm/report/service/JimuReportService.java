package com.xinyirun.scm.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.report.entity.JimuReport;
import com.xinyirun.scm.report.vo.ReportQueryVo;
import com.xinyirun.scm.report.vo.ReportResultVo;

import java.util.List;
import java.util.Map;

/**
 * 积木报表业务接口
 *
 * @author SCM Team
 * @since 2025-01-22
 */
public interface JimuReportService extends IService<JimuReport> {
    
    /**
     * 根据报表ID获取完整报表信息
     * 
     * @param reportId 报表ID
     * @return 报表详情
     */
    JimuReport getReportById(String reportId);
    
    /**
     * 根据报表编码获取报表信息
     *
     * @param code 报表编码
     * @return 报表信息
     */
    JimuReport getReportByCode(String code);
    
    /**
     * 获取报表列表
     *
     * @param queryVo 查询条件
     * @return 报表列表
     */
    List<JimuReport> getReportList(ReportQueryVo queryVo);
    
    /**
     * 保存报表
     *
     * @param report 报表信息
     * @return 保存结果
     */
    boolean saveReport(JimuReport report);
    
    /**
     * 更新报表
     *
     * @param report 报表信息
     * @return 更新结果
     */
    boolean updateReport(JimuReport report);
    
    /**
     * 删除报表
     *
     * @param reportId 报表ID
     * @return 删除结果
     */
    boolean deleteReport(String reportId);
    
    /**
     * 执行报表查询并返回数据
     *
     * @param reportId 报表ID
     * @param params 查询参数
     * @return 报表数据
     */
    ReportResultVo executeReport(String reportId, Map<String, Object> params);
    
    /**
     * 预览报表
     *
     * @param reportId 报表ID
     * @param params 参数
     * @return 预览数据
     */
    Map<String, Object> previewReport(String reportId, Map<String, Object> params);
    
    /**
     * 导出报表数据
     *
     * @param reportId 报表ID
     * @param params 参数
     * @param exportType 导出类型 (excel, pdf)
     * @return 文件字节数组
     */
    byte[] exportReport(String reportId, Map<String, Object> params, String exportType);
    
    /**
     * 增加报表浏览次数
     *
     * @param reportId 报表ID
     */
    void increaseViewCount(String reportId);
    
    /**
     * 发布报表
     *
     * @param reportId 报表ID
     * @return 发布结果
     */
    boolean publishReport(String reportId);
    
    /**
     * 取消发布报表
     *
     * @param reportId 报表ID
     * @return 取消发布结果
     */
    boolean unpublishReport(String reportId);
}