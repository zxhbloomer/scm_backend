package com.xinyirun.scm.bean.api.vo.business;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 是否可以作废
 * </p>
 *
 * @author wwl
 * @since 2022-02-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiCanceledInputVo implements Serializable {

    private static final long serialVersionUID = -2611656119311596172L;

    /**
     * 单号
     */
    private String code;

}
