package com.xinyirun.scm.bean.api.vo.business.monitor;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 承运订单
 * </p>
 *
 * @author wwl
 * @since 2023-04-06
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiCarriageOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -388420215760099641L;

    /**
     * 订单编号
     */
    private String order_no;


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
    private Integer type_id;

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
    private String transport_type_name;

    /**
     * 起始地
     */
    private String origin_place;

    /**
     * 目的地
     */
    private String destination_place;

    /**
     * 签订日期
     */
    private String sign_dt;

    /**
     * 截止日期
     */
    private String deadline_dt;

    /**
     * 运距
     */
    private BigDecimal haulDistance;

    /**
     * 销售合同号
     */
    private String sales_contract_code;

    /**
     * 付款方式
     */
    private String payType;

    /**
     * 商品
     */
    List<ApiCarriageOrderGoodsVo> commodityList;

    /**
     * 总货值
     */
    private BigDecimal totalAmount;

}
