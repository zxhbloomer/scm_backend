package com.xinyirun.scm.bean.entity.busniess.settlement;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购结算明细-源单-按采购入库结算
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_settlement_detail_source_inbound")
public class BPoSettlementDetailSourceInboundEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -8199150334938516980L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购结算主表id
     */
    @TableField("po_settlement_id")
    private Integer po_settlement_id;

    /**
     * 采购结算主表code
     */
    @TableField("po_settlement_code")
    private String po_settlement_code;

    /**
     * 合同ID
     */
    @TableField("po_contract_id")
    private Integer po_contract_id;

    /**
     * 合同编码
     */
    @TableField("po_contract_code")
    private String po_contract_code;

    /**
     * 订单ID
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 订单编码
     */
    @TableField("po_order_code")
    private String po_order_code;

    /**
     * 订单明细ID
     */
    @TableField("po_order_detail_id")
    private Integer po_order_detail_id;

    /**
     * 商品code
     */
    @TableField("goods_code")
    private String goods_code;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goods_name;

    /**
     * 物料规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编码
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    private String sku_name;

    /**
     * 订单单价
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
     * 结算单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 应结-结算数量
     */
    @TableField("planned_qty")
    private BigDecimal planned_qty;

    /**
     * 应结-结算重量
     */
    @TableField("planned_weight")
    private BigDecimal planned_weight;

    /**
     * 应结-结算体积
     */
    @TableField("planned_volume")
    private BigDecimal planned_volume;

    /**
     * 实际结算-结算数量
     */
    @TableField("settled_qty")
    private BigDecimal settled_qty;

    /**
     * 实际结算-结算重量
     */
    @TableField("settled_weight")
    private BigDecimal settled_weight;

    /**
     * 实际结算-结算体积
     */
    @TableField("settled_volume")
    private BigDecimal settled_volume;

    /**
     * 实际结算-结算金额
     */
    @TableField("settled_amount")
    private BigDecimal settled_amount;

    /**
     * 结算处理中数量
     */
    @TableField("processing_qty")
    private BigDecimal processing_qty;

    /**
     * 结算处理中重量
     */
    @TableField("processing_weight")
    private BigDecimal processing_weight;

    /**
     * 结算处理中体积
     */
    @TableField("processing_volume")
    private BigDecimal processing_volume;

    /**
     * 结算待处理数量
     */
    @TableField("unprocessed_qty")
    private BigDecimal unprocessed_qty;

    /**
     * 结算待处理重量
     */
    @TableField("unprocessed_weight")
    private BigDecimal unprocessed_weight;

    /**
     * 结算待处理体积
     */
    @TableField("unprocessed_volume")
    private BigDecimal unprocessed_volume;

    /**
     * 结算已处理(出/入)库数量
     */
    @TableField("processed_qty")
    private BigDecimal processed_qty;

    /**
     * 结算已处理(出/入)库重量
     */
    @TableField("processed_weight")
    private BigDecimal processed_weight;

    /**
     * 结算已处理(出/入)库体积
     */
    @TableField("processed_volume")
    private BigDecimal processed_volume;

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
    @TableField(value="dbversion")
    private Integer dbversion;
}