package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
// @ApiModel(value = "用户基本信息", description = "用户基本信息vo_bean")
@EqualsAndHashCode(callSuper=false)
public class UserInfoVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 574627344179000681L;

    private String token;

//    private List<SRoleVo> roles;
    private String[] roles;

    private String introduction;

    private String avatar;

    private String name;

    /**
     * AI会话UUID
     */
    private String conv_uuid;

    private UserSessionBo user_session_bean;
}
