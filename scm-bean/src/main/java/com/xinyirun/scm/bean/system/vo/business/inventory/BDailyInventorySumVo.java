package com.xinyirun.scm.bean.system.vo.business.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 每日库存表
 * </p>
 *
 * @author wwl
 * @since 2022-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDailyInventorySumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 200038300015184972L;

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
     * 调整数量
     */
    private BigDecimal qty_adjust;

    /**
     * 实时货值
     */
    private BigDecimal realtime_amount;

}
