package com.xinyirun.scm.bean.system.vo.business.check;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 盘点操作明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "盘点操作明细", description = "盘点操作明细")
public class BCheckOperateDetailVo implements Serializable {

    private static final long serialVersionUID = 2488374309984772824L;
    /**
     * id
     */
    private Integer id;

    /**
     * 盘点单id
     */
    private Integer check_operate_id;

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

    /**
     * 盘点重量
     */
    private BigDecimal qty_check;

    /**
     * 盘盈盘亏
     */
    private BigDecimal qty_diff;

    /**
     * 详情是否显示输入框
     */
    private Boolean seen = false;


}
