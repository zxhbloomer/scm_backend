package com.xinyirun.scm.bean.api.vo.business.out;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库超发校验参数
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiOutCheckVo implements Serializable {

    private static final long serialVersionUID = -381407983670069955L;

    /**
     * 出库计划编号
     */
    private String houseOutPlanCode;

    /**
     * 放货指令编号
     */
    private String houseOutDirectCode;

    /**
     * 类型 0-放货指令 1-借货指令
     */
    private String orderType;

    /**
     * 出库吨数量
     */
    private BigDecimal outNum;

}
