package com.xinyirun.scm.bean.system.vo.business.rtwo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *   生产配方_原材料
 * </p>
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BRtWoMaterialVo implements Serializable {

    private static final long serialVersionUID = -4877150346759473728L;

    private String code;

    private Integer wo_id;

    private Integer sku_id;

    private String sku_code;

    /**
     * 配比， 即为配方表 qty
     */
    private BigDecimal wo_router;

    private Integer src_warehouse_id;

    private String src_warehouse_code;

    private Integer src_location_id;

    private String src_location_code;

    private Integer src_bin_id;

    private String src_bin_code;

    private Integer allocate_id;

    /**
     * 领料出库数量
     */
    private BigDecimal wo_qty = BigDecimal.ZERO;

    private Integer warehouse_id;

    private String warehouse_code;

    private String warehouse_name;

    private Integer location_id;

    private String location_code;

    private Integer bin_id;

    private String bin_code;

    private Integer unit_id;

    private String unit_name;

    private Integer b_out_plan_id;

    /**
     * 商品属性
     */
    private String goods_prop;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格
     */
    private String spec;

    private Integer id;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;
}
