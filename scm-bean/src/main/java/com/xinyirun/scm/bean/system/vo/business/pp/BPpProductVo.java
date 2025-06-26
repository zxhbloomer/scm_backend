package com.xinyirun.scm.bean.system.vo.business.pp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 生产计划_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BPpProductVo implements Serializable {


    
    private static final long serialVersionUID = 6897392819667421911L;

    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 类型, 1产成品, 2副产品
     */
    private String type;

    /**
     * 生产计划id
     */
    private Integer pp_id;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

    /**
     * 配比
     */
    private BigDecimal pp_router;

    /**
     * 数量
     */
    private BigDecimal qty = BigDecimal.ZERO;

    private BigDecimal wo_qty = BigDecimal.ZERO;

    /**
     *待领取
     */
    private BigDecimal wo_unclaimed = BigDecimal.ZERO;



    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区code
     */
    private String location_code;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位code
     */
    private String bin_code;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 仓库名称
     */
    private String warehouse_name;

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
     * 规格
     */
    private Integer goods_id;

    /**
     * 规格
     */
    private String goods_code;

    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;
}
