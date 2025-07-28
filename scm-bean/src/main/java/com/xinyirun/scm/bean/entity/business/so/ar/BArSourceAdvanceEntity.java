package com.xinyirun.scm.bean.entity.business.so.ar;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收账款关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_source_advance")
public class BArSourceAdvanceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5759429204273384831L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 应收账款主表id
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    @TableField("ar_code")
    private String ar_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    @TableField("type")
    private String type;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    private String so_contract_code;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    private String so_order_code;

    /**
     * 销售订单id
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    @TableField("so_goods")
    private String so_goods;

    /**
     * 总数量
     */
    @TableField("qty_total")
    private BigDecimal qty_total;

    /**
     * 总金额
     */
    @TableField("amount_total")
    private BigDecimal amount_total;

    /**
     * 累计预收款金额
     */
    @TableField("so_advance_payment_amount")
    private BigDecimal so_advance_payment_amount;

    /**
     * 本次申请金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

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

}