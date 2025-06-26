package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.app.config.base.AppBaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 用户主表
 * </p>
 *
 * @author wwl
 * @since 2022-01-11
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "密码信息", description = "密码信息")
@EqualsAndHashCode(callSuper=false)
public class PasswordVo extends AppBaseVo implements Serializable {

    private static final long serialVersionUID = 119489643374099616L;

    /**
     * 历史密码
     */
    private String pwd_his_pwd;

    /**
     * 历史密码-加密后
     */
    private String encode_pwd_his_pwd;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 密码-加密后
     */
    private String encode_pwd;
}
