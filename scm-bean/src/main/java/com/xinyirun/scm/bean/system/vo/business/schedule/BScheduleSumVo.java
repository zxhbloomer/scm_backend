package com.xinyirun.scm.bean.system.vo.business.schedule;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BScheduleSumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 8990931542246349870L;

    /**
     * 已发货数量
     */
    private BigDecimal out_operated_qty;

    /**
     * 已收货数量
     */
    private BigDecimal in_operated_qty;

    /**
     * 超发数量
     */
    private BigDecimal out_over_qty;

    /**
     * 超收数量
     */
    private BigDecimal in_over_qty;

    /**
     * 退货数量
     */
    private BigDecimal count_return_qty;

}
