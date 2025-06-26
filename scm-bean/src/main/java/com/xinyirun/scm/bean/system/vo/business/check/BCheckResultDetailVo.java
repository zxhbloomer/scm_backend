package com.xinyirun.scm.bean.system.vo.business.check;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 盘盈盘亏明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_check_result_detail")
public class BCheckResultDetailVo implements Serializable {

    private static final long serialVersionUID = 2659728174133841509L;
    /**
     * id
     */
    private Integer id;

    /**
     * 盘盈盘亏单id
     */
    private Integer check_result_id;

    /**
     * 库存id
     */
    private Integer inventory_id;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 库存重量
     */
    private BigDecimal qty;

    /**
     * 盘点库存重量
     */
    private BigDecimal qty_check;

    /**
     * 盘盈盘亏
     */
    private BigDecimal qty_diff;


}
