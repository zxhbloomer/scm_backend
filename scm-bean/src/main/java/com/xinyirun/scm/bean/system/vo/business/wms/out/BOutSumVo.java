package com.xinyirun.scm.bean.system.vo.business.wms.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库单
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库单", description = "出库单")
public class BOutSumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 3398204427679271058L;

    /**
     * 出库数量
     */
    private BigDecimal actual_count;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 同步失败数量
     */
    private Integer sync_error_count;

    /**
     * 退货数量总数
     */
    private BigDecimal count_return_qty;

    /**
     * 扣减退货的真实数量
     */
    private BigDecimal actual_count_return;

}
