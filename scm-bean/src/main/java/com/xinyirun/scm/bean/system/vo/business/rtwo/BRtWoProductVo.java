package com.xinyirun.scm.bean.system.vo.business.rtwo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 生产管理_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BRtWoProductVo implements Serializable {

    private static final long serialVersionUID = -4374862589273209200L;

    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 产品类型, 1产成品, 2副产品
     */
    private String type;

    /**
     * 主表 主键 id
     */
    private Integer wo_id;

    /**
     * 商品 sku_id
     */
    private Integer sku_id;

    /**
     * 商品 sku_code
     */
    private String sku_code;

    /**
     * 配方表 数量
     */
    private BigDecimal wo_router;

    private BigDecimal wo_qty = BigDecimal.ZERO;

    /**
     * 仓库ID
     */
    private Integer warehouse_id;

    /**
     * 仓库编码
     */
    private String warehouse_code;
    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 库区 ID
     */
    private Integer location_id;

    /**
     * 库区编码
     */
    private String location_code;

    /**
     * 货位 Id
     */
    private Integer bin_id;

    /**
     * 货位 code
     */
    private String bin_code;

    /**
     * 单位 ID
     */
    private Integer unit_id;

    /**
     * 单位 名称
     */
    private String unit_name;

    private Integer b_in_plan_id;

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

    /**
     * 产品类型
     */
    private String type_name;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;
}
