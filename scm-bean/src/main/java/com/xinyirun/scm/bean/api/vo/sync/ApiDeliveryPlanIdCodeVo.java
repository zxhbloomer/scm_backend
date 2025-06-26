package com.xinyirun.scm.bean.api.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 提货单编号、id的bean
 * </p>
 *
 * @author zxh
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiDeliveryPlanIdCodeVo implements Serializable {

    private static final long serialVersionUID = -4711083285991672084L;
    /**
     * 计划id
     */
    private Integer plan_id;

    /**
     * 计划编号
     */
    private String plan_code;

    /**
     * 提货单id
     */
    private Integer delivery_id;

    /**
     * 入库单号
     */
    private String  delivery_code;


}
