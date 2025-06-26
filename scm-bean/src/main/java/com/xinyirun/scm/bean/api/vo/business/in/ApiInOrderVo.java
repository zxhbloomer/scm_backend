package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库订单
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库订单", description = "入库订单")
public class ApiInOrderVo implements Serializable {

    private static final long serialVersionUID = 4929107134971935673L;

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
     * 合同截至日期
     */
    private LocalDateTime contract_expire_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 备注
     */
    private String remark;
}
