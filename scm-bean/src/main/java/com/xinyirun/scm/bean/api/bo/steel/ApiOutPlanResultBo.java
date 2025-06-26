package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 返回出库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回出库计划", description = "返回出库计划")
public class ApiOutPlanResultBo implements Serializable {

    private static final long serialVersionUID = 5626396164798143060L;

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
     * 类型Code(WMS对接使用)0 销售出库 1采购退货出库 2直采出库
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
     * 状态Code(WMS对接) 0 进行中 1 作废 2 已完成
     */
    private String statusCode;

    /**
     * 审核时间
     */
    private String auditTime;

    /**
     * 出库计划明细
     */
    private List<ApiOutPlanDetailResultBo> wmsHouseOutPlanItemDtoList;


    /**
     * 出库单
     */
    private List<ApiOutResultBo> wmsHouseOutDocDtoList;

}
