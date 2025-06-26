package com.xinyirun.scm.bean.app.bo.jwt.user;

import com.xinyirun.scm.bean.app.bo.sys.AppSysInfoBo;
import com.xinyirun.scm.bean.app.vo.master.user.AppMStaffVo;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户的session数据
 * @ClassName: UserSessionBo
 * @Description:
 * @Author: zxh
 * @date: 2019/11/14
 * @Version: 1.0
 */
@Data
// @ApiModel(value = "app用户jwt", description = "app用户jwt")
@EqualsAndHashCode(callSuper=false)
public class AppUserBo extends AppJwtBaseBo implements Serializable {

    private static final long serialVersionUID = 1259832086997051769L;
    /**
     * 用户 信息
     */
    private MUserEntity app_user_info;
    /**
     * 员工 信息
     */
    private AppMStaffVo app_staff_info;

    /**
     * 系统参数
     */
    private AppSysInfoBo app_sys_Info;
}
