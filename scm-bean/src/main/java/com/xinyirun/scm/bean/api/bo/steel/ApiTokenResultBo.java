package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * token响应
 * </p>
 *
 * @author wwl
 * @since 2021-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "token响应", description = "token响应")
public class ApiTokenResultBo implements Serializable {

    private static final long serialVersionUID = -5835454076295008236L;

    /**
     * access_token
     */
    private String access_token;

    /**
     * token_type
     */
    private String token_type;

    /**
     * refresh_token
     */
    private String refresh_token;

    /**
     * expires_in
     */
    private Integer expires_in;

    /**
     * scope
     */
    private String scope;

    /**
     * license
     */
    private String license;

    /**
     * user
     */
    private ApiTokenResultUserBo user;

}
