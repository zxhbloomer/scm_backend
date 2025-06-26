package com.xinyirun.scm.bean.api.vo.business.out;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 出库超发校验参数
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiOutCheckResultVo implements Serializable {

    private static final long serialVersionUID = -3101544242504990791L;

    /**
     *  状态码
     */
    private String code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回结果
     */
    private String data;

    /**
     * error
     */
    private String error;

    /**
     * type
     */
    private String type;

}
