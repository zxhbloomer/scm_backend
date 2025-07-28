package com.xinyirun.scm.bean.entity.business.wms.inventory;

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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_daily_average_price")
public class BDailyAveragePriceEntity implements Serializable {

    private static final long serialVersionUID = 7719661290198860307L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 物料规格code
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库区code
     */
    @TableField("location_code")
    private String location_code;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 库位code
     */
    @TableField("bin_code")
    private String bin_code;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 是否为转换后物料
     */
    @TableField("is_convert")
    private Boolean is_convert;

    /**
     * 时间
     */
    @TableField("dt")
    private LocalDateTime dt;


}
