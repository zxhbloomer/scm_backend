package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 返回出库计划明细
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回出库计划明细", description = "返回出库计划明细")
public class ApiOutPlanDetailResultBo implements Serializable {

    private static final long serialVersionUID = -3218096855473145419L;
    /**
     * 出库计划明细Code
     */
    private String planItemCode;

    /**
     * 出库计划明细Code
     */
    private String noticeItemCode;

    /**
     * 出库计划明细id
     */
    private Integer plan_detail_id;

    /**
     * 销售订单商品Code/采购退货单商品Code
     */
    private String orderCommodityCode;

    /**
     * 商品规格id(goods_spec.id)
     */
    private String goodsSpecCode;

    /**
     * 计划出库数量(未出库数量+已出库数量)
     */
    private BigDecimal planOutNum;

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
