package com.xinyirun.scm.bean.entity.busniess.po.ap;

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
 * 应付账款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_total")
public class BApTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -7501393719592926759L;


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应付账款主表ID
     */
    @TableField("ap_id")
    private Integer ap_id;

    /**
     * 申请付款总金额
     */
    @TableField("payable_amount_total")
    private BigDecimal payable_amount_total;

    /**
     * 已付款总金额
     */
    @TableField("paid_amount_total")
    private BigDecimal paid_amount_total;

    /**
     * 付款中总金额
     */
    @TableField("paying_amount_total")
    private BigDecimal paying_amount_total;

    /**
     * 未付款总金额
     */
    @TableField("unpay_amount_total")
    private BigDecimal unpay_amount_total;    /**
     * 中止总金额
     */
    @TableField("stoppay_amount_total")
    private BigDecimal stoppay_amount_total;

    /**
     * 取消付款总金额
     */
    @TableField("cancelpay_amount_total")
    private BigDecimal cancelpay_amount_total;

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
