package com.xinyirun.scm.bean.app.vo.master.user;

import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
// @ApiModel(value = "用户基本信息", description = "用户基本信息vo_bean")
@EqualsAndHashCode(callSuper=false)
public class AppUserInfoVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 574627344179000681L;

    private String token;

    private String[] roles;

    private String introduction;

    private String avatar;

    private String name;

    private AppUserBo user_bean;
}
