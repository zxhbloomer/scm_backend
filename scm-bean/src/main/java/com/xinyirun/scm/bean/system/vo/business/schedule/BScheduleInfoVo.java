package com.xinyirun.scm.bean.system.vo.business.schedule;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 调度
 * </p>
 *
 * @author wwl
 * @since 2022-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BScheduleInfoVo implements Serializable {

    private static final long serialVersionUID = 8196175370124129224L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 物流订单id
     */
    private Integer schedule_id;

    /**
     * 物流合同号
     */
    private String waybill_contract_no;

    /**
     * 承运商id
     */
    private Integer customer_id;

    /**
     * 承运商名称
     */
    private String customer_name;

    /**
     * 承运商code
     */
    private String customer_code;

}
