package com.xinyirun.scm.bean.entity.busniess.inventory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存表
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_daily_inventory_price")
public class BDailyInventoryPriceEntity implements Serializable {

    private static final long serialVersionUID = -6826949030008347701L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 日期
     */
    @TableField("dt")
    private LocalDateTime dt;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 仓库编码
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 仓库名称
     */
    @TableField("warehouse_name")
    private String warehouse_name;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库区编码
     */
    @TableField("location_code")
    private String location_code;

    /**
     * 库区名称
     */
    @TableField("location_name")
    private String location_name;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 库位编码
     */
    @TableField("bin_code")
    private String bin_code;

    /**
     * 库位名称
     */
    @TableField("bin_name")
    private String bin_name;

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
     * 规格名称
     */
    @TableField("sku_name")
    private String sku_name;

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
     * 货主名称
     */
    @TableField("owner_name")
    private String owner_name;

    /**
     * 数量
     */
    @TableField("qty")
    private BigDecimal qty;

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
     * 单位
     */
    @TableField("unit_name")
    private String unit_name;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField("u_time")
    private LocalDateTime u_time;


}
