package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 返回入库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回入库计划", description = "返回入库计划")
public class ApiInPlanResultBo implements Serializable {

    private static final long serialVersionUID = -4209060632091299267L;

    /**
     * 计划code
     */
    private String planCode;

    /**
     * 计划code
     */
    private String plan_code;

    /**
     * 计划id
     */
    private Integer plan_id;

    private Integer plan_detail_id;

    private String plan_detail_code;

    /**
     * code
     */
    private String noticeCode;

    /**
     * 类型Code(WMS对接使用) 0 采购入库 1销售退货入库 2提货入库
     */
    private String typeCode;

    /**
     * 订单Code/退货单Code 根据入库类型判断
     */
    private String orderCode;

    /**
     * 货主id(WMS对接过来数据 wms.id)
     */
    private Integer ownerCargoId;

    /**
     * 货主code
     */
    private String ownerCargoCode;

    /**
     * 货主名称
     */
    private String ownerCargoName;


    /**
     * 状态Code(WMS对接)0 进行中 1 作废 2 已完成
     */
    private String statusCode;

    /**
     * 审核时间
     */
    private String auditTime;

    /**
     * 入库计划明细
     */
    private List<ApiInPlanDetailResultBo> wmsHousePutPlanItemDtoList;


    /**
     * 入库单
     */
    private List<ApiInResultBo> wmsHousePutDocDtoList;
}
