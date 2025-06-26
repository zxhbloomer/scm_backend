package com.xinyirun.scm.bean.system.bo.session.user.system;

import com.xinyirun.scm.bean.system.bo.sys.SysInfoBo;
import com.xinyirun.scm.bean.system.bo.sys.app.SAppConfigBo;
import com.xinyirun.scm.bean.system.config.base.SessionBaseBean;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;

// import io.swagger.annotations.ApiModel;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 用户的session数据
 * @ClassName: UserSessionBo
 * @Description:
 * @Author: zxh
 * @date: 2019/11/14
 * @Version: 1.0
 */
@Data
// @ApiModel(value = "用户session", description = "用户session")
@EqualsAndHashCode(callSuper=false)
public class UserSessionBo extends SessionBaseBean implements Serializable {

    private static final long serialVersionUID = 4115465265205543377L;

    /**
     * 用户 信息
     */
    private MUserEntity user_info;
    /**
     * 员工 信息
     */
    private MStaffVo staff_info;

    /**
     * 权限数据
     */
    private List<SRoleVo> roles;

    /**
     * 系统参数
     */
    private SysInfoBo sys_Info;

    /**
     * app配置表
     */
    private SAppConfigBo appConfigBo;
}
