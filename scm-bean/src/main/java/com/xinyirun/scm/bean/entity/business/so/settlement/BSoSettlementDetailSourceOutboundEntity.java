package com.xinyirun.scm.bean.entity.business.so.settlement;

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
 * 销售结算明细-源单-按销售出库结算实体类
 * 
 * @author Claude Code
 * @since 2024-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_settlement_detail_source_outbound")
public class BSoSettlementDetailSourceOutboundEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 787402712786956069L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售结算主表id
     */
    @TableField("so_settlement_id")
    @DataChangeLabelAnnotation("销售结算主表id")
    private Integer so_settlement_id;

    /**
     * 销售结算主表code
     */
    @TableField("so_settlement_code")
    @DataChangeLabelAnnotation("销售结算编号")
    private String so_settlement_code;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    @DataChangeLabelAnnotation("销售合同id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    @DataChangeLabelAnnotation("销售合同编号")
    private String so_contract_code;

    /**
     * 销售订单id
     */
    @TableField("so_order_id")
    @DataChangeLabelAnnotation("销售订单id")
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    @DataChangeLabelAnnotation("销售订单编号")
    private String so_order_code;

    /**
     * 销售订单明细id
     */
    @TableField("so_order_detail_id")
    @DataChangeLabelAnnotation("销售订单明细id")
    private Integer so_order_detail_id;

    /**
     * 商品编号
     */
    @TableField("goods_code")
    @DataChangeLabelAnnotation("商品编号")
    private String goods_code;

    /**
     * 商品id
     */
    @TableField("goods_id")
    @DataChangeLabelAnnotation("商品id")
    private Integer goods_id;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    @DataChangeLabelAnnotation("商品名称")
    private String goods_name;

    /**
     * SKU id
     */
    @TableField("sku_id")
    @DataChangeLabelAnnotation("SKU id")
    private Integer sku_id;

    /**
     * SKU编号
     */
    @TableField("sku_code")
    @DataChangeLabelAnnotation("SKU编号")
    private String sku_code;

    /**
     * SKU名称
     */
    @TableField("sku_name")
    @DataChangeLabelAnnotation("SKU名称")
    private String sku_name;

    /**
     * 订单价格
     */
    @TableField("order_price")
    @DataChangeLabelAnnotation("订单价格")
    private BigDecimal order_price;

    /**
     * 订单数量
     */
    @TableField("order_qty")
    @DataChangeLabelAnnotation("订单数量")
    private BigDecimal order_qty;

    /**
     * 订单金额
     */
    @TableField("order_amount")
    @DataChangeLabelAnnotation("订单金额")
    private BigDecimal order_amount;

    /**
     * 应结算价格
     */
    @TableField("planned_price")
    @DataChangeLabelAnnotation("应结算价格")
    private BigDecimal planned_price;

    /**
     * 应结算数量
     */
    @TableField("planned_qty")
    @DataChangeLabelAnnotation("应结算数量")
    private BigDecimal planned_qty;

    /**
     * 应结算重量
     */
    @TableField("planned_weight")
    @DataChangeLabelAnnotation("应结算重量")
    private BigDecimal planned_weight;

    /**
     * 应结算体积
     */
    @TableField("planned_volume")
    @DataChangeLabelAnnotation("应结算体积")
    private BigDecimal planned_volume;

    /**
     * 应结算金额
     */
    @TableField("planned_amount")
    @DataChangeLabelAnnotation("应结算金额")
    private BigDecimal planned_amount;

    /**
     * 实结算价格
     */
    @TableField("settled_price")
    @DataChangeLabelAnnotation("实结算价格")
    private BigDecimal settled_price;

    /**
     * 实结算数量
     */
    @TableField("settled_qty")
    @DataChangeLabelAnnotation("实结算数量")
    private BigDecimal settled_qty;

    /**
     * 实结算重量
     */
    @TableField("settled_weight")
    @DataChangeLabelAnnotation("实结算重量")
    private BigDecimal settled_weight;

    /**
     * 实结算体积
     */
    @TableField("settled_volume")
    @DataChangeLabelAnnotation("实结算体积")
    private BigDecimal settled_volume;

    /**
     * 实结算金额
     */
    @TableField("settled_amount")
    @DataChangeLabelAnnotation("实结算金额")
    private BigDecimal settled_amount;

    /**
     * 处理中数量
     */
    @TableField("processing_qty")
    @DataChangeLabelAnnotation("处理中数量")
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    @TableField("processing_weight")
    @DataChangeLabelAnnotation("处理中重量")
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    @TableField("processing_volume")
    @DataChangeLabelAnnotation("处理中体积")
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    @TableField("unprocessed_qty")
    @DataChangeLabelAnnotation("未处理数量")
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    @TableField("unprocessed_weight")
    @DataChangeLabelAnnotation("未处理重量")
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    @TableField("unprocessed_volume")
    @DataChangeLabelAnnotation("未处理体积")
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    @TableField("processed_qty")
    @DataChangeLabelAnnotation("已处理数量")
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    @TableField("processed_weight")
    @DataChangeLabelAnnotation("已处理重量")
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    @TableField("processed_volume")
    @DataChangeLabelAnnotation("已处理体积")
    private BigDecimal processed_volume;

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
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    @Version
    private Integer dbversion;
}