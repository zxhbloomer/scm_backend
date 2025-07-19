package com.xinyirun.scm.bean.entity.busniess.wms.inplan;

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
 * 入库计划明细
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_in_plan_detail")
public class BInPlanDetailEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 7792096593736356237L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

    /**
     * 序号
     */
    @TableField("no")
    private Integer no;

    /**
     * 入库计划id
     */
    @TableField("in_plan_id")
    private Integer in_plan_id;

    /**
     * 串号id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 串号编码
     */
    @TableField("serial_code")
    private String serial_code;

    /**
     * 串号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 项目编码
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Integer contract_id;

    /**
     * 合同编码
     */
    @TableField("contract_code")
    private String contract_code;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer order_id;

    /**
     * 订单编码
     */
    @TableField("order_code")
    private String order_code;

    /**
     * 订单明细id
     */
    @TableField("order_detail_id")
    private Integer order_detail_id;

    /**
     * 商品编码
     */
    @TableField("goods_code")
    private String goods_code;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * sku id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * sku编码
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 订单价格
     */
    @TableField("order_price")
    private BigDecimal order_price;

    /**
     * 订单数量
     */
    @TableField("order_qty")
    private BigDecimal order_qty;

    /**
     * 订单金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;

    /**
     * 数量
     */
    @TableField("qty")
    private BigDecimal qty;

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
     * 库位id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 供应商id
     */
    @TableField("supplier_id")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    private String supplier_code;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 处理中数量
     */
    @TableField("processing_qty")
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    @TableField("processing_weight")
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    @TableField("processing_volume")
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    @TableField("unprocessed_qty")
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    @TableField("unprocessed_weight")
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    @TableField("unprocessed_volume")
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    @TableField("processed_qty")
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    @TableField("processed_weight")
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    @TableField("processed_volume")
    private BigDecimal processed_volume;

    /**
     * 作废数量
     */
    @TableField("cancel_qty")
    private BigDecimal cancel_qty;

    /**
     * 作废重量
     */
    @TableField("cancel_weight")
    private BigDecimal cancel_weight;

    /**
     * 作废体积
     */
    @TableField("cancel_volume")
    private BigDecimal cancel_volume;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

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
}
