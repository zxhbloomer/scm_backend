package com.xinyirun.scm.bean.entity.business.so.arrefund;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 应收退款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_refund_total")
public class BArReFundTotalEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -7501393719592926761L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收退款主表ID
     */
    @TableField("ar_refund_id")
    private Integer ar_refund_id;

    /**
     * 销售订单ID
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 申请退款总金额
     */
    @TableField("refundable_amount_total")
    private BigDecimal refundable_amount_total;

    /**
     * 已退款总金额
     */
    @TableField("refunded_amount_total")
    private BigDecimal refunded_amount_total;

    /**
     * 退款中总金额
     */
    @TableField("refunding_amount_total")
    private BigDecimal refunding_amount_total;

    /**
     * 未退款总金额
     */
    @TableField("unrefund_amount_total")
    private BigDecimal unrefund_amount_total;

    /**
     * 取消退款总金额
     */
    @TableField("cancelrefund_amount_total")
    private BigDecimal cancelrefund_amount_total;

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
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}