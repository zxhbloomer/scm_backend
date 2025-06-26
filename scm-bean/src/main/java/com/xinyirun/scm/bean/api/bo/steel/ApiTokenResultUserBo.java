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
// @ApiModel(value = "用户信息", description = "用户信息")
public class ApiTokenResultUserBo implements Serializable {

    private static final long serialVersionUID = -1810611338937866789L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 名称
     */
    private String realName;

    /**
     * 部门id
     */
    private Integer departmentId;

    /**
     * 部门名称
     */
    private Integer departmentName;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否可用
     */
    private Boolean enabled;

    /**
     * accountNonExpired
     */
    private Boolean accountNonExpired;

    /**
     * accountNonLocked
     */
    private Boolean accountNonLocked;

    /**
     * authorities
     */
    private String[] authorities;

}
