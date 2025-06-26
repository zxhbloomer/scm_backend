package com.xinyirun.scm.bean.entity.master.inventory;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *  库存流水表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_inventory_account")
public class MInventoryAccountEntity implements Serializable {

    private static final long serialVersionUID = -5749649022955269383L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * '类型 0 入  1 出  2 调整'
     */
    @TableField("type")
    private String type;

    /**
     * 流水类型
     *     IN_CREATE("10", "入库单生成"),                    // 数量进入锁定库存
     *     IN_AGREE("11", "入库单审核同意"),                 // 锁定库存转入可用库存，锁定库存释放
     *     IN_NOT_CANCEL("12", "入库单审核驳回"),            // 锁定库存释放
     *     IN_CANCEL("13", "入库单作废"),                   // 制单时：锁定时库存释放，审核通过时可用库存释放
     *     OUT_CREATE("20", "出库单生成"),
     *     OUT_AGREE("21", "出库单生成审核同意"),
     *     OUT_NOT_AGREE("22", "出库单审核驳回"),
     *     OUT_CANCEL("23", "出库单作废"),
     *     ADJUST_CREATE("30", "调整单生成"),
     *     ADJUST_AGREE("31", "调整单审核同意"),
     *     ADJUST_NOT_AGREE("32", "调整单审核驳回"),
     *     ADJUST_CANCELLED("33", "调整单作废"),
     */
    @TableField("business_type")
    private String business_type;

    /**
     * 关联单号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 关联单号编号
     */
    @TableField("serial_id")
    private Integer serial_id;

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
     * 库存id
     */
    @TableField("inventory_id")
    private Integer inventory_id;

    /**
     * 流水数量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 完成后库存总量
     */
    @TableField("qty_inventory")
    private BigDecimal qty_inventory;

    /**
     * 完成后锁定库存总量
     */
    @TableField("qty_lock_inventory")
    private BigDecimal qty_lock_inventory;

    /**
     * 可用库存差额
     */
    @TableField("qty_diff")
    private BigDecimal qty_diff;

    /**
     * 锁定库存差额
     */
    @TableField("qty_lock_diff")
    private BigDecimal qty_lock_diff;

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


}
