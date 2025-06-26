package com.xinyirun.scm.bean.system.vo.wms.in.delivery;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 提货单合计信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDeliverySumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1820686959232797387L;

    /**
     * 提货数量
     */
    private BigDecimal actual_count;

    /**
     * 提货金额
     */
    private BigDecimal amount;


}
