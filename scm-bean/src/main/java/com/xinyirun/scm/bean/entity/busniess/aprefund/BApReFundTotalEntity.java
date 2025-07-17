package com.xinyirun.scm.bean.entity.busniess.aprefund;

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
 * 应付账款退款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_total")
public class BApReFundTotalEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -7501393719592926760L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应付账款退款主表ID
     */
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 采购订单主表ID
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 可退款总金额
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