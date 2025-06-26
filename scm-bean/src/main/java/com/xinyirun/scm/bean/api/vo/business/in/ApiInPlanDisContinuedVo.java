package com.xinyirun.scm.bean.api.vo.business.in;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Wqf
 * @Description: 入库通知中止 VO
 * @CreateTime : 2024/1/5 16:44
 */

@Data
public class ApiInPlanDisContinuedVo implements Serializable {

    
    private static final long serialVersionUID = -4577973260343773224L;

    /**
     * 外部关联单号
     */
    private String code;
}
