package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 入库返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库返回结果", description = "入库返回结果")
public class ApiInResultVo implements Serializable {

    private static final long serialVersionUID = 5040666833129469339L;

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
     * 入库计划返回结果对象
     */
    private ApiInPlanResultVo in_plan_vo;

    /**
     * 入库单返回结果对象
     */
    private ApiInBillResultVo in_bill_vo;
}
