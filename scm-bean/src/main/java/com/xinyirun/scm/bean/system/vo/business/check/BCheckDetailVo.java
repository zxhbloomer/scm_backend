package com.xinyirun.scm.bean.system.vo.business.check;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 盘点任务明细
 * </p>
 *
 * @author htt
 * @since 2021-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "盘点任务明细", description = "盘点任务明细")
public class BCheckDetailVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -6990734003560168335L;

    /**
     * id
     */
    private Integer id;

    /**
     * 盘点单id
     */
    private Integer check_id;

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
     * 规格名称
     */
    private String sku_name;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 库存重量
     */
    private BigDecimal qty;


}
