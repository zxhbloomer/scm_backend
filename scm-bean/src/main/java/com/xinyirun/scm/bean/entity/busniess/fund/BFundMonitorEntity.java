package com.xinyirun.scm.bean.entity.busniess.fund;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 资金流水监控表
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_fund_monitor")
public class BFundMonitorEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 655424785373732857L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 类型：0-冻结；1-生效
     */
    @TableField("type")
    private String type;    /**
     * YUFU_PAY_INC	预付款金额增加
     * YUFU_PAY_DEC	预付款金额减少
     * YUFU_RTN_INC	预付款退回金额增加
     * YUFU_RTN_DEC	预付款退回金额减少
     */
    @TableField("business_type")
    private String business_type;

    /**
     * 业务类型名称
     */
    @TableField("business_type_name")
    private String business_type_name;

    /**
     * 关联单号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 关联单号id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 关联单号code
     */
    @TableField("serial_code")
    private String serial_code;

    /**
     * 企业id
     */
    @TableField("enterprise_id")
    private Integer enterprise_id;

    /**
     * 企业code
     */
    @TableField("enterprise_code")
    private String enterprise_code;

    /**
     * 企业银行账户id
     */
    @TableField("bank_account_id")
    private Integer bank_account_id;

    /**
     * 企业银行账户code
     */
    @TableField("bank_account_code")
    private String bank_account_code;

    /**
     * 款项类型id
     */
    @TableField("bank_accounts_type_id")
    private Integer bank_accounts_type_id;

    /**
     * 款想类型code
     */
    @TableField("bank_accounts_type_code")
    private String bank_accounts_type_code;

    /**
     * 资金类型（0：资金池；1：专款专用）
     */
    @TableField("fund_type")
    private String fund_type;

    /**
     * 交易id
     */
    @TableField("trade_id")
    private Integer trade_id;

    /**
     * 交易编号
     */
    @TableField("trade_code")
    private String trade_code;

    /**
     * 业务类型（表名）
     */
    @TableField("trade_type")
    private String trade_type;    /**
     * 交易订单id（采购订单、销售订单）
     */
    @TableField("trade_order_id")
    private String trade_order_id;

    /**
     * 交易订单编号（采购/销售订单）
     */
    @TableField("trade_order_code")
    private String trade_order_code;

    /**
     * 交易订单类型：表名
     */
    @TableField("trade_order_type")
    private String trade_order_type;    /**
     * 交易合同id（采购/销售合同）
     */
    @TableField("trade_contract_id")
    private String trade_contract_id;

    /**
     * 交易合同编号（采购/销售合同）
     */
    @TableField("trade_contract_code")
    private String trade_contract_code;

    /**
     * 交易合同类型：表名
     */
    @TableField("trade_contract_type")
    private String trade_contract_type;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建人id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改人id")
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

}
