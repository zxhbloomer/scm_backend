package com.xinyirun.scm.bean.system.vo.business.so.settlement;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 销售结算明细-源单-按销售出库结算
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BSoSettlementDetailSourceOutboundVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -957620373491623796L;
    
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 销售结算主表id
     */
    private Integer so_settlement_id;

    /**
     * 销售结算主表code
     */
    private String so_settlement_code;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 商品编号
     */
    private String goods_code;

    /**
     * 商品id
     */
    private Integer goods_id;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * SKU id
     */
    private Integer sku_id;

    /**
     * SKU编号
     */
    private String sku_code;

    /**
     * SKU名称
     */
    private String sku_name;

    /**
     * 订单价格
     */
    private BigDecimal order_price;

    /**
     * 订单数量
     */
    private BigDecimal order_qty;

    /**
     * 订单金额
     */
    private BigDecimal order_amount;

    /**
     * 应结算价格
     */
    private BigDecimal planned_price;

    /**
     * 应结算数量
     */
    private BigDecimal planned_qty;

    /**
     * 应结算重量
     */
    private BigDecimal planned_weight;

    /**
     * 应结算体积
     */
    private BigDecimal planned_volume;

    /**
     * 应结算金额
     */
    private BigDecimal planned_amount;

    /**
     * 实结算价格
     */
    private BigDecimal settled_price;

    /**
     * 实结算数量
     */
    private BigDecimal settled_qty;

    /**
     * 实结算重量
     */
    private BigDecimal settled_weight;

    /**
     * 实结算体积
     */
    private BigDecimal settled_volume;

    /**
     * 实结算金额
     */
    private BigDecimal settled_amount;

    /**
     * 处理中数量
     */
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    private BigDecimal processed_volume;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
    
    /**
     * 销售订单明细id
     */
    private Integer so_order_detail_id;
}