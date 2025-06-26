package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 出库计划同步
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划同步", description = "出库计划同步")
public class ApiOutPlanSyncVo implements Serializable {

    private static final long serialVersionUID = 4735105784959957560L;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 序号
     */
    private String code;

    /**
     * 出库单号
     */
    private String in_code;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 出库计划结果对象
     */
    private ApiOutPlanResultVo outPlanResult;

    /**
     * 出库计划详情结果对象
     */
    private ApiOutPlanDetailResultVo outPlanDetailResult;

    /**
     * 出库单结果对象
     */
    private ApiOutBillResultVo outBillResult;
}
