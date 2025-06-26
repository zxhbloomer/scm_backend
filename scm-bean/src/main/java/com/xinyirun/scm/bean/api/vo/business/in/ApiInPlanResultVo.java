package com.xinyirun.scm.bean.api.vo.business.in;


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
 * 入库计划返回结果对象
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库计划返回结果对象", description = "入库计划返回结果对象")
public class ApiInPlanResultVo implements Serializable {

    private static final long serialVersionUID = -6958986136010824534L;

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
     * 入库类型
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
     * 单据类型：0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 供应商名
     */
    private String supplier_name;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 入库计划详情返回结果集合
     */
    private List<ApiInPlanDetailResultVo> resultList;

}
