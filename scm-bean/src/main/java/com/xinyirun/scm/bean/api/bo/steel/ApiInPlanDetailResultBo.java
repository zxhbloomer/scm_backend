package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 返回入库计划明细
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回入库计划明细", description = "返回入库计划明细")
public class ApiInPlanDetailResultBo implements Serializable {

    private static final long serialVersionUID = 1995765245005386081L;

    /**
     * 入库计划明细Code
     */
    private String planItemCode;

    /**
     * 入库计划明细Code
     */
    private String noticeItemCode;

    /**
     * 入库计划明细id
     */
    private Integer plan_detail_id;

    /**
     * 采购订单商品code/销售退货订单商品code
     */
    private String orderCommodityCode;

    /**
     * 商品规格id(goods_spec.id)
     */
    private String goodsSpecCode;

    /**
     * 计划入库数量(未入库数量+已入库数量)
     */
    private BigDecimal planPutNum;


    /**
     * 仓库id(WMS对接过来 wms.id)
     */
    private Integer houseId;

    /**
     * 仓库code
     */
    private String houseCode;

    /**
     * 仓库名称
     */
    private String houseName;
}
