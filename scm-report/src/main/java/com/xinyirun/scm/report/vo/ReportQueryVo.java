package com.xinyirun.scm.report.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报表查询条件VO
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
public class ReportQueryVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 7205111139657495690L;

    /**
     * 报表名称（模糊查询）
     */
    private String name;
    
    /**
     * 报表编码（模糊查询）
     */
    private String code;
    
    /**
     * 报表状态：0-设计中，1-已发布
     */
    private String status;
    
    /**
     * 报表类型
     */
    private String type;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页面大小
     */
    private Integer pageSize = 10;
}