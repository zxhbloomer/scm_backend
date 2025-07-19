package com.xinyirun.scm.bean.system.vo.business.wms.out;


import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划合同
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划合同", description = "出库计划合同")
public class BOutPlanContractVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 5545103188724788197L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 委托单主表id
     */
    private Integer plan_id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户编码
     */
    private String client_code;
}
