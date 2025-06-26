package com.xinyirun.scm.bean.system.vo.business.out.receive;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 收货单合计信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BReceiveSumVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8440144432858346365L;
    /**
     * 提货数量
     */
    private BigDecimal actual_count;

    /**
     * 提货金额
     */
    private BigDecimal amount;


}
