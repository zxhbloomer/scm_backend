package com.xinyirun.scm.bean.entity.busniess.wms.in;

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
 * 入库单
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_in")
public class BInEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 5135040159165381571L;

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
     * 入库类型：0=采购入库 1=调拨入库 2=退货入库 3=监管入库 4=普通入库 5=生产入库
     */
    @TableField("type")
    private String type;

    /**
     * 入库状态：0制单，1已提交，2审核通过，3审核驳回，4已入库，5作废
     */
    @TableField("status")
    private String status;

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
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主编码
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 入库计划单id
     */
    @TableField("plan_id")
    private Integer plan_id;

    /**
     * 入库计划明细单id
     */
    @TableField("plan_detail_id")
    private Integer plan_detail_id;

    /**
     * 委托方id
     */
    @TableField("consignor_id")
    private Integer consignor_id;

    /**
     * 委托方编码
     */
    @TableField("consignor_code")
    private String consignor_code;

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
     * 入库仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 入库库区id
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
     * 物料编码
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 计划数量
     */
    @TableField("plan_qty")
    private BigDecimal plan_qty;

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
     * 实际数量（净重）
     */
    @TableField("actual_qty")
    private BigDecimal actual_qty;

    /**
     * 入库数量
     */
    @TableField("qty")
    private BigDecimal qty;

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
     * 入库金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 入库单位
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 入库时间
     */
    @TableField("inbound_time")
    private LocalDateTime inbound_time;

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
     * 待处理数量
     */
    @TableField("unprocessed_qty")
    private BigDecimal unprocessed_qty;

    /**
     * 待处理重量
     */
    @TableField("unprocessed_weight")
    private BigDecimal unprocessed_weight;

    /**
     * 待处理体积
     */
    @TableField("unprocessed_volume")
    private BigDecimal unprocessed_volume;

    /**
     * 已处理(出/入)库数量
     */
    @TableField("processed_qty")
    private BigDecimal processed_qty;

    /**
     * 已处理(出/入)库重量
     */
    @TableField("processed_weight")
    private BigDecimal processed_weight;

    /**
     * 已处理(出/入)库体积
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
     * 库存流水id
     */
    @TableField("inventory_sequence_id")
    private Integer inventory_sequence_id;

    /**
     * 车牌号
     */
    @TableField("vehicle_no")
    private String vehicle_no;

    /**
     * 皮重
     */
    @TableField("tare_weight")
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    @TableField("gross_weight")
    private BigDecimal gross_weight;

    /**
     * 原发数量
     */
    @TableField("original_qty")
    private BigDecimal original_qty;

    /**
     * 实收车数
     */
    @TableField("cart_count")
    private Integer cart_count;

    /**
     * 是否异常:false否;true:是
     */
    @TableField("is_exception")
    private Boolean is_exception;

    /**
     * 异常描述
     */
    @TableField("exception_comment")
    private String exception_comment;

    /**
     * 物料是否合格:true合格,false不合格
     */
    @TableField("stock_status")
    private Boolean stock_status;

    /**
     * 下一个审批人姓名
     */
    @TableField("next_approve_name")
    private String next_approve_name;

    /**
     * BPM实例id
     */
    @TableField("bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * BPM实例编码
     */
    @TableField("bpm_instance_code")
    private String bpm_instance_code;

    /**
     * BPM流程名称
     */
    @TableField("bpm_process_name")
    private String bpm_process_name;

    /**
     * BPM取消实例id
     */
    @TableField("bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * BPM取消实例编码
     */
    @TableField("bpm_cancel_instance_code")
    private String bpm_cancel_instance_code;

    /**
     * BPM取消流程名称
     */
    @TableField("bpm_cancel_process_name")
    private String bpm_cancel_process_name;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value = "dbversion")
    private Integer dbversion;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    private Boolean is_del;

}
