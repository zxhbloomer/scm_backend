package com.xinyirun.scm.bean.entity.business.schedule;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 调度
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_schedule")
public class BScheduleEntity implements Serializable {

    private static final long serialVersionUID = 3838334726290929713L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物流订单单号
     */
    @TableField("code")
    private String code;

    /**
     * 类型 1:物流订单 2:物流调度 3:物流直达 4:直采入库 5:直销出库
     */
    @TableField("type")
    private String type;

    /**
     * 单据状态
     */
    @TableField("status")
    private String status;

    /**
     * 物流订单运输方式：0公运、1铁运、2水运
     */
    @TableField("transport_type")
    private String transport_type;

    /**
     * 合同号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 发货类型:0出库，1提货
     */
    @TableField("out_type")
    private String out_type;

    /**
     * 收货类型:0入库，1卸货
     */
    @TableField("in_type")
    private String in_type;

    /**
     * 入库计划生成方式
     */
    @TableField("in_rule")
    private String in_rule;

    /**
     * 出库计划单号
     */
    @TableField("out_plan_code")
    private String out_plan_code;

    /**
     * 入库计划单号
     */
    @TableField("in_plan_code")
    private String in_plan_code;

    /**
     * 出库计划明细单号
     */
    @TableField("out_plan_detail_code")
    private String out_plan_detail_code;

    /**
     * 入库计划明细单号
     */
    @TableField("in_plan_detail_code")
    private String in_plan_detail_code;

    /**
     * 出库计划明细id
     */
    @TableField("out_plan_detail_id")
    private Integer out_plan_detail_id;

    /**
     * 入库计划明细id
     */
    @TableField("in_plan_detail_id")
    private Integer in_plan_detail_id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer order_id;

    /**
     * 物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 物料code
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 发货仓库id
     */
    @TableField("out_warehouse_id")
    private Integer out_warehouse_id;

    /**
     * 发货库区id
     */
    @TableField("out_location_id")
    private Integer out_location_id;

    /**
     * 发货库位id
     */
    @TableField("out_bin_id")
    private Integer out_bin_id;

    /**
     * 收货仓库id
     */
    @TableField("in_warehouse_id")
    private Integer in_warehouse_id;

    /**
     * 收货库区id
     */
    @TableField("in_location_id")
    private Integer in_location_id;

    /**
     * 收货库位id
     */
    @TableField("in_bin_id")
    private Integer in_bin_id;

    /**
     * 计划出库数量
     */
    @TableField("out_schedule_qty")
    private BigDecimal out_schedule_qty;

    /**
     * 已出库数量
     */
    @TableField("out_operated_qty")
    private BigDecimal out_operated_qty;

    /**
     * 待出库数量
     */
    @TableField("out_balance_qty")
    private BigDecimal out_balance_qty;

    /**
     * 出库单位id
     */
    @TableField("out_unit_id")
    private Integer out_unit_id;

    /**
     * 计划入库数量
     */
    @TableField("in_schedule_qty")
    private BigDecimal in_schedule_qty;

    /**
     * 已入库数量
     */
    @TableField("in_operated_qty")
    private BigDecimal in_operated_qty;

    /**
     * 待入库数量
     */
    @TableField("in_balance_qty")
    private BigDecimal in_balance_qty;

    /**
     * 入库单位id
     */
    @TableField("in_unit_id")
    private Integer in_unit_id;

    /**
     * 委托方id-入
     */
    @TableField("in_consignor_id")
    private Integer in_consignor_id;

    /**
     * 委托方code-入
     */
    @TableField("in_consignor_code")
    private String in_consignor_code;

    /**
     * 货主id-入
     */
    @TableField("in_owner_id")
    private Integer in_owner_id;

    /**
     * 货主code-入
     */
    @TableField("in_owner_code")
    private String in_owner_code;

    /**
     * 详细地址-入
     */
    @TableField("in_warehouse_address")
    private String in_warehouse_address;

    /**
     * 委托方id-出
     */
    @TableField("out_consignor_id")
    private Integer out_consignor_id;

    /**
     * 委托方code-出
     */
    @TableField("out_consignor_code")
    private String out_consignor_code;

    /**
     * 货主id-出
     */
    @TableField("out_owner_id")
    private Integer out_owner_id;

    /**
     * 货主code-出
     */
    @TableField("out_owner_code")
    private String out_owner_code;

    /**
     * 详细地址-出
     */
    @TableField("out_warehouse_address")
    private String out_warehouse_address;

    /**
     * 出库计划生成方式 0 系统生成 1 手动选择
     */
    @TableField("out_rule")
    private String out_rule;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;
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
     * 是否删除, 0否, 1是
     */
    @TableField(value="is_delete")
    private String is_delete = "0";

    /**
     * 作废理由
     */
    @TableField(value="remark")
    private String remark;

    @TableField(value = "monitor_count")
    private Integer monitor_count;

    @TableField(value = "carriage_order_id")
    private Integer carriage_order_id;

    /**
     * 是否消费, 0 执行中, 1 未执行
     */
    @TableField(value = "is_consumer")
    private String is_consumer;
}
