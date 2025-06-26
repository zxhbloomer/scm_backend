package com.xinyirun.scm.bean.system.vo.business.materialconvert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2022-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BAveragePriceVo implements Serializable {

    private static final long serialVersionUID = -649163830208466101L;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 换算后数量
     */
    private BigDecimal actual_weight;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

}
