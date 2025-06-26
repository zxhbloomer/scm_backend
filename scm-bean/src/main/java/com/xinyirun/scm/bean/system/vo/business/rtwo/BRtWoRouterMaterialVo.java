package com.xinyirun.scm.bean.system.vo.business.rtwo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 生产配方_原材料
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BRtWoRouterMaterialVo implements Serializable {

    private static final long serialVersionUID = 5404132937703912989L;

    /**
     * 主键
     */
    private Integer id;

    private String code;

    /**
     * router表主键
     */
    private Integer router_id;

    private Integer sku_id;

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
     * 消耗数量
     */
    private BigDecimal qty;

    /**
     * 商品属性
     */
    private String goods_prop;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 商品单位
     */
    private Integer unit_id;

    /**
     * 单位名称
     */
    private String unit_name;

}
