package com.xinyirun.scm.bean.entity.business.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 承运订单work表
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wk_co")
public class BWkCoEntity implements Serializable {

    private static final long serialVersionUID = -7268917688806892897L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String order_no;

    /**
     * 承运合同编号
     */
    @TableField("carriage_contract_code")
    private String carriage_contract_code;

    /**
     * 状态(30-执行中 35-已完成 40-已作废 enum.id)
     */
    @TableField("status")
    private String status;

    /**
     * 承运合同-合同类型id(dictionary.id)
     */
    @TableField("type_id")
    private String type_id;

    /**
     * 合同类型名称
     */
    @TableField("type_name")
    private String type_name;

    /**
     * 承运人企业名称
     */
    @TableField("company_name")
    private String company_name;

    /**
     * 承运人企业信用代码
     */
    @TableField("company_credit_no")
    private String company_credit_no;

    /**
     * 托运人组织主体名称
     */
    @TableField("org_name")
    private String org_name;

    /**
     * 托运人组织主体信用代码
     */
    @TableField("org_credit_no")
    private String org_credit_no;

    /**
     * 承运订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 运输总数量
     */
    @TableField("num")
    private BigDecimal num;

    /**
     * 运费单价(含税)
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 税率
     */
    @TableField("rate")
    private BigDecimal rate;

    /**
     * 运费总金额=总货值
     */
    @TableField("transport_amount")
    private BigDecimal transport_amount;

    /**
     * 运费总金额(不含税)
     */
    @TableField("transport_amount_not")
    private BigDecimal transport_amount_not;

    /**
     * 运费总税款金额
     */
    @TableField("transport_amount_tax")
    private BigDecimal transport_amount_tax;

    /**
     * 总货值
     */
    @TableField("total_amount")
    private BigDecimal total_amount;

    /**
     * 运输方式
     */
    @TableField("transport_type_name")
    private String transport_type_name;

    /**
     * 起始地
     */
    @TableField("origin_place")
    private String origin_place;

    /**
     * 目的地
     */
    @TableField("destination_place")
    private String destination_place;

    /**
     * 签订日期
     */
    @TableField("sign_dt")
    private String sign_dt;

    /**
     * 截止日期
     */
    @TableField("deadline_dt")
    private String deadline_dt;

    /**
     * 运距
     */
    @TableField("haul_distance")
    private BigDecimal haul_distance;

    /**
     * 销售合同号
     */
    @TableField("sales_contract_code")
    private String sales_contract_code;

    /**
     * 付款方式
     */
    @TableField("pay_type")
    private String pay_type;
}
