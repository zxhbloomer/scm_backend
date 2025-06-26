package com.xinyirun.scm.bean.system.vo.business.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库计划列表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutPlanSumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 436190020159927542L;

    /**
     * 数量
     */
    private BigDecimal count;

    /**
     * 换算后数量
     */
    private BigDecimal has_handle_count;

    /**
     * 待出库理数量
     */
    private BigDecimal pending_count;

    /**
     * 同步异常数量
     */
    private Integer sync_error_count;

    /**
     * 退货数量总数
     */
    private BigDecimal count_return_qty;

}
