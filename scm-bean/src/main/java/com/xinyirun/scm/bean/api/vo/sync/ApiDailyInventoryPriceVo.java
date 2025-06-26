package com.xinyirun.scm.bean.api.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 每日货值
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiDailyInventoryPriceVo implements Serializable {

    private static final long serialVersionUID = -1503185986118606556L;

    /**
     * 日期
     */
    private String dt;

    /**
     * 仓库id
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
     * 仓库简称
     */
    private String warehouse_simple_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区编码
     */
    private String location_code;

    /**
     * 库区名称
     */
    private String location_name;

    /**
     * 库区简称
     */
    private String location_simple_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位编码
     */
    private String bin_code;

    /**
     * 库位名称
     */
    private String bin_name;

    /**
     * 库位简称
     */
    private String bin_simple_name;

    /**
     * 板块id
     */
    private Integer business_id;

    /**
     * 板块code
     */
    private String business_code;

    /**
     * 板块名称
     */
    private String business_name;

    /**
     * 行业code
     */
    private String industry_code;

    /**
     * 行业ID
     */
    private Integer industry_id;

    /**
     * 行业名称
     */
    private String industry_name;

    /**
     * 类别id
     */
    private Integer category_id;

    /**
     * 类别code
     */
    private String category_code;

    /**
     * 类别名称
     */
    private String category_name;

    /**
     * 商品id
     */
    private Integer goods_id;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 商品code
     */
    private String goods_code;

    /**
     * 物料规格id
     */
    private Integer sku_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 商品属性id
     */
    private Integer prop_id;

    /**
     * 商品属性名称
     */
    private String prop_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 货主简称
     */
    private String owner_simple_name;

    /**
     * 库存
     */
    private BigDecimal qty;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

    /**
     * 单位
     */
    private String unit_name;

    /**
     * 创建时间
     */
    private String c_time;

    /**
     * 修改时间
     */
    private String u_time;

}
