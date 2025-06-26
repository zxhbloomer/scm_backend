package com.xinyirun.scm.bean.system.vo.master.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MInventorySumVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 9115628856551899536L;
    /**
     * 库存数量
     */
    private BigDecimal qty_avaible;


}
