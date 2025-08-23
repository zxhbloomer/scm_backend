package com.xinyirun.scm.report.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 报表执行结果VO
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
public class ReportResultVo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 报表ID
     */
    private String reportId;
    
    /**
     * 报表编码
     */
    private String reportCode;
    
    /**
     * 报表名称
     */
    private String reportName;
    
    /**
     * 执行状态：success-成功, error-失败
     */
    private String status;
    
    /**
     * 错误信息（执行失败时）
     */
    private String errorMessage;
    
    /**
     * 报表数据
     */
    private List<Map<String, Object>> dataList;
    
    /**
     * 数据总条数
     */
    private Long total;
    
    /**
     * 报表JSON设计数据
     */
    private String jsonStr;
    
    /**
     * 报表参数配置
     */
    private Map<String, Object> params;
    
    /**
     * 执行时间（毫秒）
     */
    private Long executeTime;
    
    /**
     * 是否有更多数据
     */
    private Boolean hasMore;
    
    /**
     * 分页信息
     */
    private PageInfo pageInfo;
    
    @Data
    public static class PageInfo implements Serializable {
        private Integer pageNum = 1;
        private Integer pageSize = 100;
        private Long total = 0L;
        private Integer totalPages = 0;
    }
}