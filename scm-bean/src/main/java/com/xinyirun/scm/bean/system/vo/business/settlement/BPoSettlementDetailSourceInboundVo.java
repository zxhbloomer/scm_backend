package com.xinyirun.scm.bean.system.vo.business.settlement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购结算明细-源单-按采购入库结算
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BPoSettlementDetailSourceInboundVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -957620373491623795L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 采购结算主表id
     */
    private Integer po_settlement_id;

    /**
     * 采购结算主表code
     */
    private String po_settlement_code;

    /**
     * 合同ID
     */
    private Integer po_contract_id;

    /**
     * 合同编码
     */
    private String po_contract_code;

    /**
     * 订单ID
     */
    private Integer po_order_id;

    /**
     * 订单编码
     */
    private String po_order_code;

    /**
     * 商品code
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
     * 物料规格id
     */
    private Integer sku_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 订单单价
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
     * 结算单价
     */
    private BigDecimal price;

    /**
     * 应结-结算数量
     */
    private BigDecimal planned_qty;

    /**
     * 应结-结算重量
     */
    private BigDecimal planned_weight;

    /**
     * 应结-结算体积
     */
    private BigDecimal planned_volume;

    /**
     * 实际结算-结算数量
     */
    private BigDecimal settled_qty;

    /**
     * 实际结算-结算重量
     */
    private BigDecimal settled_weight;

    /**
     * 实际结算-结算体积
     */
    private BigDecimal settled_volume;

    /**
     * 实际结算-结算金额
     */
    private BigDecimal settled_amount;

    /**
     * 结算处理中数量
     */
    private BigDecimal processing_qty;

    /**
     * 结算处理中重量
     */
    private BigDecimal processing_weight;

    /**
     * 结算处理中体积
     */
    private BigDecimal processing_volume;

    /**
     * 结算待处理数量
     */
    private BigDecimal unprocessed_qty;

    /**
     * 结算待处理重量
     */
    private BigDecimal unprocessed_weight;

    /**
     * 结算待处理体积
     */
    private BigDecimal unprocessed_volume;

    /**
     * 结算已处理(出/入)库数量
     */
    private BigDecimal processed_qty;

    /**
     * 结算已处理(出/入)库重量
     */
    private BigDecimal processed_weight;

    /**
     * 结算已处理(出/入)库体积
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
} 