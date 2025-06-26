package com.xinyirun.scm.bean.system.vo.business.wo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 生产配方_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BWoRouterProductVo implements Serializable {

    private static final long serialVersionUID = -8291381240197477914L;

    /**
     * 主键id
     */
    private Integer id;

    private String code;

    /**
     * 类型, 产成品, 副产品
     */
    private String type;
    private String type_name;

    /**
     * router_id, wo_router表
     */
    private Integer router_id;

    /**
     * 商品id
     */
    private Integer sku_id;

    /**
     * 商品code
     */
    private String sku_code;

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
     * 型规
     */
    private String type_gauge;

    /**
     * 配比
     */
    private BigDecimal router;

    /**
     * 商品属性
     */
    private String goods_prop;

    /**
     * 商品id
     */
    private Integer goods_id;

    /**
     * 物料编码
     */
    private String goods_code;

}
