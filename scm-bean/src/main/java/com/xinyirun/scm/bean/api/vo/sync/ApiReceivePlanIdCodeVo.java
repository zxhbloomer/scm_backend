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
public class ApiReceivePlanIdCodeVo implements Serializable {

    private static final long serialVersionUID = -6912175800742785899L;
    /**
     * 计划id
     */
    private Integer plan_id;

    /**
     * 计划编号
     */
    private String plan_code;

    /**
     * 收货单id
     */
    private Integer receive_id;

    /**
     * 收货单号
     */
    private String receive_code;

    /**
     * 中止
     */
    private Boolean discontinue;


}
