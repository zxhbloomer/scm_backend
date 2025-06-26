package com.xinyirun.scm.bean.api.vo.business.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 承运订单
 * </p>
 *
 * @author wwl
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBWkCoVo implements Serializable {

    private static final long serialVersionUID = 7956743554994284138L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 承运合同编号
     */
    private String carriage_contract_code;

    /**
     * 状态(30-执行中 35-已完成 40-已作废 enum.id)
     */
    private String status;

    /**
     * 承运合同-合同类型id(dictionary.id)
     */
    private String type_id;

    /**
     * 合同类型名称
     */
    private String type_name;

    /**
     * 承运人企业名称
     */
    private String company_name;

    /**
     * 承运人企业信用代码
     */
    private String company_credit_no;

    /**
     * 托运人组织主体名称
     */
    private String org_name;

    /**
     * 托运人组织主体信用代码
     */
    private String org_credit_no;

    /**
     * 承运订单备注
     */
    private String remark;

    /**
     * 运输总数量
     */
    private BigDecimal num;

    /**
     * 运费单价(含税)
     */
    private BigDecimal price;

    /**
     * 税率
     */
    private BigDecimal rate;

    /**
     * 运费总金额=总货值
     */
    private BigDecimal transport_amount;

    /**
     * 运费总金额(不含税)
     */
    private BigDecimal transport_amount_not;

    /**
     * 运费总税款金额
     */
    private BigDecimal transport_amount_tax;

    /**
     * 运输方式
     */
    private String delivery_type_name;

    /**
     * 起始地
     */
    private String origin_place;

    /**
     * 目的地
     */
    private String destination_place;

    /**
     * 错误类型 1:订单编号为空 2:承运合同为空 3:承运人企业信用代码 4:托运人组织主体信用代码
     */
    private String flag;


}
