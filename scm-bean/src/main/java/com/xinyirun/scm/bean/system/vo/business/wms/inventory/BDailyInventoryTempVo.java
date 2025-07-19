package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDailyInventoryTempVo implements Serializable {


    private static final long serialVersionUID = 2275508742162918673L;
    /**
     * id
     */
    private Integer id;

    /**
     * 日期
     */
    private LocalDateTime dt;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库存数量
     */
    private BigDecimal qty;

    /**
     * 入库量
     */
    private BigDecimal qty_in;

    /**
     * 出库量
     */
    private BigDecimal qty_out;

    /**
     * 调整量
     */
    private BigDecimal qty_adjust;

    /**
     * 移动加权货值单价
     */
    private BigDecimal price;

    /**
     * 库存货值
     */
    private BigDecimal amount;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 实时货值总价
     */
    private BigDecimal realtime_amount;

    /**
     * 实时单价
     */
    private BigDecimal realtime_price;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

}
