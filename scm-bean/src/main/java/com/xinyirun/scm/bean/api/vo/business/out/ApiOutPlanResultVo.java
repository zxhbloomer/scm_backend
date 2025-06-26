package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库计划返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划返回结果", description = "出库计划返回结果")
public class ApiOutPlanResultVo implements Serializable {

    private static final long serialVersionUID = 8717869181609442651L;

    /**
     * 计划单号
     */
    private String code;

    /**
     * 委托方名
     */
    private String consignor_name;

    /**
     * 委托方编码
     */
    private String consignor_code;

    /**
     * 货主名
     */
    private String owner_name;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 出库类型
     */
    private String type;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 客户名
     */
    private String client_name;

    /**
     * 客户编码
     */
    private String client_code;

    /**
     * 出库计划详情返回结果集合
     */
    private List<ApiOutPlanDetailResultVo> resultList;
}
