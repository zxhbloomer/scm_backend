package com.xinyirun.scm.bean.api.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 入库编号、id的bean
 * </p>
 *
 * @author zxh
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiInPlanIdCodeSettlementedVo implements Serializable {

    private static final long serialVersionUID = 5698303548936148556L;
    /**
     * 计划id
     */
    private Integer plan_id;

    /**
     * 计划编号
     */
    private String plan_code;


}
