package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 入库计划同步
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库计划同步", description = "入库计划同步")
public class ApiInPlanSyncVo implements Serializable {

    private static final long serialVersionUID = 5001964131488729864L;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 序号
     */
    private String code;

    /**
     * 入库单号
     */
    private String in_code;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 入库计划结果对象
     */
    private ApiInPlanResultVo inPlanResult;

    /**
     * 入库计划详情结果对象
     */
    private ApiInPlanDetailResultVo inPlanDetailResult;

    /**
     * 入库单结果对象
     */
    private ApiInBillResultVo inBillResult;

}
