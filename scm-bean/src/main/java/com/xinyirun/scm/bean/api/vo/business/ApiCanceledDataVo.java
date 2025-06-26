package com.xinyirun.scm.bean.api.vo.business;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 是否可作废
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiCanceledDataVo implements Serializable {


    private static final long serialVersionUID = 5480921806030306878L;

    /**
     * 是否结算： false 未结算     true 已结算
     */
    private Boolean cancel;

    /**
     * code
     */
    private String code;

    /**
     * remark
     */
    private String remark;

}
