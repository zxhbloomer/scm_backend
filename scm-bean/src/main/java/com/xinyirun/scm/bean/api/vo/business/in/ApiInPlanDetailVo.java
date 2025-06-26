package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 入库计划详情
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库计划详情", description = "入库计划详情")
public class ApiInPlanDetailVo implements Serializable {

    private static final long serialVersionUID = 774100992525492483L;

    /**
     * 编号
     */
    private String code;

    /**
     * 订单明细编号
     */
    private String no;
	
	/**
     * 订单商品编号
     */
    private String order_commodity_code;

    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 别称
     */
    private String nickname;

    /**
     * 规格编号
     */
    private String spec_code;

    /**
     * 物料单价
     */
    private BigDecimal price;

    /**
     * 订单价格
     */
    private BigDecimal order_price;

    /**
     *  外部接口传入的量
     */
    private BigDecimal count;

    /**
     * 品名
     */
    private String pm;

    /**
     * 单位
     */
    private String unit;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 是否数量浮动管控
     */
    private Boolean float_controled;

    /**
     * 上浮百分比
     */
    private BigDecimal float_up;

    /**
     * 下浮百分比
     */
    private BigDecimal float_down;

    /**
     * 备注
     */
    private String remark;
}
