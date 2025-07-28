package com.xinyirun.scm.bean.entity.business.so.ar;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 应收账款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_total")
public class BArTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -7501393719592926760L;


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收账款主表ID
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 应收金额总计
     */
    @TableField("receivable_amount_total")
    private BigDecimal receivable_amount_total;

    /**
     * 已收款总金额
     */
    @TableField("received_amount_total")
    private BigDecimal received_amount_total;

    /**
     * 收款中总金额
     */
    @TableField("receiving_amount_total")
    private BigDecimal receiving_amount_total;

    /**
     * 未收款总金额
     */
    @TableField("unreceive_amount_total")
    private BigDecimal unreceive_amount_total;

    /**
     * 中止收款总金额
     */
    @TableField("stopreceive_amount_total")
    private BigDecimal stopreceive_amount_total;

    /**
     * 取消收款总金额
     */
    @TableField("cancelreceive_amount_total")
    private BigDecimal cancelreceive_amount_total;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建时间", extension = "getCTimeExtension")
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;


}