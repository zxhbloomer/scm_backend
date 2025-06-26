package com.xinyirun.scm.bean.entity.busniess.out;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划详情
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_out_plan_detail")
public class BOutPlanDetailEntity implements Serializable {


    private static final long serialVersionUID = 1462422525190151883L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 序号
     */
    @TableField("no")
    private Integer no;

    /**
     * 序号
     */
    @TableField("code")
    private String code;

    /**
     * 订单明细编号
     */
    @TableField("order_detail_no")
    private String order_detail_no;

    /**
     * 外部系统单号
     */
    @TableField("extra_code")
    private String extra_code;

    /**
     * 型规
     */
    @TableField("type_gauge")
    private String type_gauge;

    /**
     * 别名
     */
    @TableField("alias")
    private String alias;

    /**
     * 计划单id
     */
    @TableField("plan_id")
    private Integer plan_id;

    /**
     * 单据状态
     */
    @TableField("status")
    private String status;

    /**
     * 原单据状态
     */
    @TableField("pre_status")
    private String pre_status;

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
     * 订单商品编号
     */
    @TableField("order_goods_code")
    private String order_goods_code;

    /**
     * 是否超发
     */
    @TableField("over_release")
    private Boolean over_release;

    /**
     * 是否锁库存
     */
    @TableField("lock_inventory")
    private Boolean lock_inventory;

    /**
     * 物料单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 数量
     */
    @TableField("count")
    private BigDecimal count;

    /**
     * 退货数量 总数
     */
    @TableField("return_qty")
    private BigDecimal return_qty;

    /**
     * 重量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 体积
     */
    @TableField("volume")
    private BigDecimal volume;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 订单Id
     */
    @TableField("order_id")
    private Integer order_id;

    /**
     * 订单类型
     */
    @TableField("order_type")
    private String order_type;

    /**
     * 待处理数量
     */
    @TableField("pending_count")
    private BigDecimal pending_count;

    /**
     * 待处理重量
     */
    @TableField("pending_weight")
    private BigDecimal pending_weight;

    /**
     * 待处理体积
     */
    @TableField("pending_volume")
    private BigDecimal pending_volume;

    /**
     * 已处理(出/入)库数量
     */
    @TableField("has_handle_count")
    private BigDecimal has_handle_count;

    /**
     * 已处理(出/入)库重量
     */
    @TableField("has_handle_weight")
    private BigDecimal has_handle_weight;

    /**
     * 已处理(出/入)库体积
     */
    @TableField("has_handle_volume")
    private BigDecimal has_handle_volume;

    /**
     * 审核人id
     */
    @TableField("auditor_id")
    private Integer auditor_id;

    /**
     * 审核时间
     */
    @TableField("audit_dt")
    private LocalDateTime audit_dt;

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
     * 审核意见
     */
    @TableField("e_opinion")
    private String e_opinion;

    /**
     * 同步状态id
     */
    @TableField("sync_id")
    private Integer sync_id;

    /**
     * 审核意见
     */
    @TableField("audit_info")
    private String audit_info;

    /**
     * 是否数量浮动管控
     */
    @TableField("over_inventory_policy")
    private Boolean over_inventory_policy;

    /**
     * 上浮百分比
     */
    @TableField("over_inventory_upper")
    private BigDecimal over_inventory_upper;

    /**
     * 下浮百分比
     */
    @TableField("over_inventory_lower")
    private BigDecimal over_inventory_lower;

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
     * 备注
     */
    @TableField("remark")
    private String remark;
}
