package com.xinyirun.scm.bean.system.vo.business.adjust;


import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调整明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存调整明细", description = "库存调整明细")
public class BAdjustDetailVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -4751641316101115287L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 单号
     */
    private String code;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 调整单id
     */
    private Integer adjust_id;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    private String warehouse_name;

    private String goods_name;

    private String spec;

    private String pm;

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
    private Integer sku_id;

    /**
     * 物料id
     */
    private String sku_code;

    /**
     * 原库存数量
     */
    private BigDecimal qty;

    /**
     * 调整库存数量
     */
    private BigDecimal qty_adjust;

    /**
     * 调整差量
     */
    private BigDecimal qty_diff;

    /**
     * 库存ID
     */
    private Integer inventory_id;

    /**
     * 库存CODE
     */
    private String inventory_code;

    /**
     * 调整后平均单价
     */
    private BigDecimal adjusted_price;

    /**
     * 调整后货值
     */
    private BigDecimal adjusted_amount;

    /**
     * 库存调整规则
     */
    private String adjusted_rule;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核时间
     */
    private LocalDateTime e_dt;

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
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


}
