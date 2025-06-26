package com.xinyirun.scm.bean.system.bo.inventory.warehouse;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存表
 * </p>
 *
 */
@Data
public class MInventoryBo implements Serializable {

    private static final long serialVersionUID = -6558174524800951765L;

    /**
     * 主键
     */
    private Integer id;

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
     * 物料id
     */
    private Integer goods_id;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 批次
     */
    private String lot;

    /**
     * 库存数量
     */
    private BigDecimal qty;

    /**
     * 可用量
     */
    private BigDecimal qty_avaible;

    /**
     * 锁定库存数量
     */
    private BigDecimal qty_lock;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

}

