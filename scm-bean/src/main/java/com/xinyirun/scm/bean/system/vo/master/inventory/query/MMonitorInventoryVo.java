package com.xinyirun.scm.bean.system.vo.master.inventory.query;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 库存监管
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MMonitorInventoryVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = 480435913841465731L;
    /**
     * 主键
     */
    private String id;

    /**
     * 仓库
     */
    private String warehouse_name;
    /**
     * 货主
     */
    private String owner_name;
    /**
     * 物料名称
     */
    private String sku_name;

    /**
     * 物料编码
     */
    private String sku_code;

    /**
     * 实际入库数量
     */
    private BigDecimal in_weight;

    /**
     * 实际出库数量
     */
    private BigDecimal out_weight;

    /**
     * 库存
     */
    private BigDecimal qty_avaible;

    /**
     * 锁定库存
     */
    private BigDecimal qty_lock;

    /**
     * 计算库存
     */
    private BigDecimal calculation_avaible;

    /**
     * 合计库存
     */
    private BigDecimal sum_avaible;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 仓库ID
     */
    private Integer warehouse_id;

    /**
     * 货主ID
     */
    private Integer owner_id;

    /**
     * 调整库存
     */
    private BigDecimal qty_diff;

}
