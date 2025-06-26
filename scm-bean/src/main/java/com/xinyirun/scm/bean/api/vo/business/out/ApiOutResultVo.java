package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 出库返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库返回结果", description = "出库返回结果")
public class ApiOutResultVo implements Serializable {

    private static final long serialVersionUID = -6185419941237644589L;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 出库计划结果对象
     */
    private ApiOutPlanResultVo outPlan;

    /**
     * 出库单结果对象
     */
    private ApiOutBillResultVo outBill;

}
