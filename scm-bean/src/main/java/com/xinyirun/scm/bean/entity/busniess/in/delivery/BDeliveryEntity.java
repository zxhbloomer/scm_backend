package com.xinyirun.scm.bean.entity.busniess.in.delivery;

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
 * 提货单
 * </p>
 *
 * @author xinyirun
 * @since 2024-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_delivery")
public class BDeliveryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8895548466550358344L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 入库单号
     */
    @TableField("code")
    private String code;

    /**
     * 入库类型：6=提货入库
     */
    @TableField("type")
    private String type;

    /**
     * 状态：0制单，1已提交，2审核通过，3审核驳回，4已入库，5作废
     */
    @TableField("status")
    private String status;

    /**
     * 原单据状态
     */
    @TableField("pre_status")
    private String pre_status;

    /**
     * 是否已结算 0否 1是
     */
    @TableField("is_settled")
    private Boolean is_settled;

    /**
     * 结算单号
     */
    @TableField("settle_code")
    private String settle_code;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 入库计划id
     */
    @TableField("plan_id")
    private Integer plan_id;

    /**
     * 入库计划明细id
     */
    @TableField("plan_detail_id")
    private Integer plan_detail_id;

    /**
     * 委托方id
     */
    @TableField("consignor_id")
    private Integer consignor_id;

    /**
     * 货主编码
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 委托方编码
     */
    @TableField("consignor_code")
    private String consignor_code;

    /**
     * 批次
     */
    @TableField("lot")
    private String lot;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 入库库位id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 入库库位id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 入库库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编码
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 计划数量
     */
    @TableField("plan_count")
    private BigDecimal plan_count;

    /**
     * 计划重量
     */
    @TableField("plan_weight")
    private BigDecimal plan_weight;

    /**
     * 计划体积
     */
    @TableField("plan_volume")
    private BigDecimal plan_volume;

    /**
     * 实际数量
     */
    @TableField("actual_count")
    private BigDecimal actual_count;

    /**
     * 实际重量
     */
    @TableField("actual_weight")
    private BigDecimal actual_weight;

    /**
     * 实际体积
     */
    @TableField("actual_volume")
    private BigDecimal actual_volume;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 货值
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 入库单位
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 换算后单位id
     */
    @TableField("tgt_unit_id")
    private Integer tgt_unit_id;

    /**
     * 同步状态id
     */
    @TableField("sync_id")
    private Integer sync_id;

    /**
     * 换算关系
     */
    @TableField("calc")
    private BigDecimal calc;

    /**
     * 入库时间
     */
    @TableField("inbound_time")
    private LocalDateTime inbound_time;

    /**
     * 收货确认单id
     */
    @TableField("receive_order_id")
    private Integer receive_order_id;

    /**
     * 库存流水id
     */
    @TableField("inventory_account_id")
    private Integer inventory_account_id;

    /**
     * 审核人id
     */
    @TableField("e_id")
    private Integer e_id;

    /**
     * 审核意见
     */
    @TableField("e_opinion")
    private String e_opinion;

    /**
     * 审核时间
     */
    @TableField("e_dt")
    private LocalDateTime e_dt;

    /**
     * 作废审核人id
     */
    @TableField("cancel_audit_id")
    private Integer cancel_audit_id;

    /**
     * 作废审核时间
     */
    @TableField("cancel_audit_dt")
    private LocalDateTime cancel_audit_dt;

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
    @TableField(value="dbversion")
    private Integer dbversion;

    @TableField(value = "vehicle_no")
    private String vehicle_no;

    @TableField(value = "tare_weight")
    private BigDecimal tare_weight;

    @TableField(value = "gross_weight")
    private BigDecimal gross_weight;
}
