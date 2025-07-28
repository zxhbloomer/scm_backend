package com.xinyirun.scm.bean.entity.business.monitor;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_carriage_order")
@Builder
public class BCarriageOrderEntity implements Serializable {

    private static final long serialVersionUID = -4424972083408189564L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 承运合同编号
     */
    @TableField("carriage_contract_code")
    private String carriageContractCode;

    /**
     * 状态(30-执行中 35-已完成 40-已作废 enum.id)
     */
    @TableField("status")
    private String status;

    /**
     * 承运合同-合同类型id(dictionary.id)
     */
    @TableField("type_id")
    private String typeId;

    /**
     * 合同类型名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 承运人企业名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 承运人企业信用代码
     */
    @TableField("company_credit_no")
    private String companyCreditNo;

    /**
     * 托运人组织主体名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 托运人组织主体信用代码
     */
    @TableField("org_credit_no")
    private String orgCreditNo;

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
    private BigDecimal transportAmount;

    /**
     * 运费总金额(不含税)
     */
    @TableField("transport_amount_not")
    private BigDecimal transportAmountNot;

    /**
     * 运费总税款金额
     */
    @TableField("transport_amount_tax")
    private BigDecimal transportAmountTax;

    /**
     * 运输方式
     */
    @TableField("transport_type_name")
    private String transportTypeName;

    /**
     * 起始地
     */
    @TableField("origin_place")
    private String originPlace;

    /**
     * 目的地
     */
    @TableField("destination_place")
    private String destinationPlace;

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
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

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

    /**
     * 总货值
     */
    @TableField("total_amount")
    private BigDecimal total_amount;

}
