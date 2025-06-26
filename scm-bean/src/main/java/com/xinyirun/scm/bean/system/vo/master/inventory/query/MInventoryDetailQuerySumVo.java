package com.xinyirun.scm.bean.system.vo.master.inventory.query;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 库存明细查询vo
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存", description = "库存")
public class MInventoryDetailQuerySumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3369287378279319716L;
    /**
     * 库存数量
     */
    private BigDecimal qty_avaible;

    /**
     * 锁定库存数量
     */
    private BigDecimal qty_lock;

    /**
     * 货值
     */
    private BigDecimal amount;

}
