package com.xinyirun.scm.bean.entity.business.so.arreceive;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收单明细表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_receive_detail")
public class BArReceiveDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6120701031468466999L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收单明细编号
     */
    @TableField("code")
    private String code;

    /**
     * 应收单id
     */
    @TableField("ar_receive_id")
    private Integer ar_receive_id;

    /**
     * 应收单code
     */
    @TableField("ar_receive_code")
    private String ar_receive_code;

    /**
     * 应收账款表.id
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 应收账款表.code
     */
    @TableField("ar_code")
    private String ar_code;

    /**
     * 应收账款明细表.id
     */
    @TableField("ar_detail_id")
    private Integer ar_detail_id;

    /**
     * 应收账款明细表.code
     */
    @TableField("ar_detail_code")
    private String ar_detail_code;

    /**
     * 企业银行账户表id
     */
    @TableField("bank_accounts_id")
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表编号
     */
    @TableField("bank_accounts_code")
    private String bank_accounts_code;

    /**
     * 应收金额
     */
    @TableField("receivable_amount")
    private BigDecimal receivable_amount;

    /**
     * 已收金额
     */
    @TableField("received_amount")
    private BigDecimal received_amount;

    /**
     * 本次收款金额
     */
    @TableField("receive_amount")
    private BigDecimal receive_amount;

    /**
     * 收款中金额
     */
    @TableField("receiving_amount")
    private BigDecimal receiving_amount;

    /**
     * 未收款金额
     */
    @TableField("unreceive_amount")
    private BigDecimal unreceive_amount;

    /**
     * 作废金额
     */
    @TableField("cancel_amount")
    private BigDecimal cancel_amount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}