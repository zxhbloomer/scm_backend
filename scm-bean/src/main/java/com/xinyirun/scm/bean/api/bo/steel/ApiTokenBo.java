package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * token
 * </p>
 *
 * @author wwl
 * @since 2021-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "token", description = "token")
public class ApiTokenBo implements Serializable {

    private static final long serialVersionUID = -3971966463033041983L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 授权类型(password)
     */
    private String grant_type;

    /**
     * 作用域(all)
     */
    private String scope;

}
