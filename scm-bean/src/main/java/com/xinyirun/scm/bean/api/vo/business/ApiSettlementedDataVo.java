package com.xinyirun.scm.bean.api.vo.business;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 入库单返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入出库单结算结果", description = "入出库单结算结果")
public class ApiSettlementedDataVo implements Serializable {

    private static final long serialVersionUID = 1776779108821928999L;

    /**
     * 是否结算： false 未结算     true 已结算
     */
    private Boolean isSettlement;

    /**
     * code
     */
    private String code;

}
