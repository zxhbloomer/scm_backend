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
public class BDailyInventoryWorkVo implements Serializable {

    private static final long serialVersionUID = -2680915300911759441L;

    /**
     * id
     */
    private Integer id;

    /**
     * 日期
     */
    private LocalDateTime dt;

    /**
     * 类型：1、入库；2、出库；3、调整
     */
    private String type;

    /**
     * 单据类型：按实际单据来判断；实际单据（入库单、出库单、调整单）
     */
    private String bill_type;

    /**
     * 计划id
     */
    private Integer plan_id;

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
     * 单价
     */
    private BigDecimal price;

    /**
     * 移动平均单价
     */
    private BigDecimal average_price;

    /**
     * 当时间节点实际库存
     */
    private BigDecimal inventory_qty;

    /**
     * 库存货值
     */
    private BigDecimal amount;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;


}
