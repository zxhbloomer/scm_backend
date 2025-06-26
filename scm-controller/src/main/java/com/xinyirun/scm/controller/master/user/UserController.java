package com.xinyirun.scm.controller.master.user;

import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionAndTopNavBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.user.*;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.JsonResultTypeConstants;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.PasswordException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.user.IMUserPermissionRbacService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author zxh
 */
@RestController
@RequestMapping(value = "/api/v1/user")
@Slf4j
// @Api(tags = "用户相关")
public class UserController extends SystemBaseController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IMUserService service;

    @Autowired
    private IMStaffService imStaffService;

    @Autowired
    private IMUserPermissionRbacService imUserPermissionRbacService;

    @SysLogAnnotion("获取用户信息")
    // @ApiOperation(value = "获取用户信息")
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<JsonResultAo<UserInfoVo>> userInfo(@RequestParam("token") String token) {

        UserInfoVo userInfoVo = service.getUserInfo(token);

        /** 设置user session bean */
        userInfoVo.setUser_session_bean(getUserSession());

        return ResponseEntity.ok().body(ResultUtil.OK(userInfoVo, JsonResultTypeConstants.NULL_NOT_OUT));
    }

    @SysLogAnnotion("获取用户信息")
    // @ApiOperation(value = "获取用户信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<UserInfoVo>> getUserInfo() {

        UserInfoVo userInfoVo = service.getUserInfo();

        /** 设置user session bean */
        userInfoVo.setUser_session_bean(getUserSession());

        MStaffVo staff_info = imStaffService.selectByid(userInfoVo.getUser_session_bean().getStaff_Id());
        userInfoVo.getUser_session_bean().setStaff_info(staff_info);

        return ResponseEntity.ok().body(ResultUtil.OK(userInfoVo, JsonResultTypeConstants.NULL_NOT_OUT));
    }

    @SysLogAnnotion("修改密码接口")
    @PostMapping("/password/update")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> updatePassword(@RequestBody(required = false) PasswordVo vo) {
        vo.setEncode_pwd(super.getPassword(vo.getPwd()));
        vo.setEncode_pwd_his_pwd(super.getPassword(vo.getPwd_his_pwd()));
        if(service.updatePwd(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("setdnnode")
    // @ApiOperation(value = "登出")
    @PostMapping("/setdnnode")
    public void setdnnode(@RequestBody(required = false) DnInfoVo dninfo) {
        redisUtil.set(DataSourceHelper.getCurrentDataSourceName() + "::" + "dnnode-"+dninfo.getUsername(), dninfo.getDnnode());
    }

    @SysLogAnnotion("登出")
    // @ApiOperation(value = "登出")
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> logout() {
        // 更新最后登出时间
        service.updateLastLogoutDate();
        return ResponseEntity.ok().body(ResultUtil.OK("登出成功"));
    }


    @SysLogAnnotion("员工主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MUserVo>> save(@RequestBody(required = false) MUserEntity bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectUserById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("员工主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MUserVo>> insert(@RequestBody(required = false) MUserEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectUserById(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("获取用户信息")
    // @ApiOperation(value = "获取用户信息")
    @PostMapping("/detail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MUserVo>> getUsrBeanById(@RequestBody(required = false) MUserVo bean) {
        MUserVo mUserVo = service.selectUserById(bean.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(mUserVo));
    }

    @SysLogAnnotion("获取用户信息")
    // @ApiOperation(value = "获取用户信息")
    @PostMapping("/getpsd")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> getUsrPsdString(@RequestBody(required = false) MUserEntity bean, HttpServletRequest request) {
        MUserVo mUserVo = service.selectUserById(bean.getId());
        if(!StringUtils.isEmpty(bean.getPwd())){
            String encodePsd = getPassword(bean.getPwd());
            // 保存到session中
            HttpSession session = request.getSession();
            session.setAttribute(SystemConstants.SESSION_KEY_USER_PASSWORD, encodePsd);
            return ResponseEntity.ok().body(ResultUtil.OK(bean.getPwd()));
        } else {
            throw new PasswordException("密码设置失败。");
        }
    }

    @SysLogAnnotion("获取顶部导航栏数据")
    // @ApiOperation(value = "获取顶部导航栏数据")
    @PostMapping("/permiss_topnav")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PermissionAndTopNavBo>> getPermissionAndSetTopNavAction(
            @RequestParam("pathOrIndex") String pathOrIndex,
            @RequestParam(required = false,name="topNavCode") String topNavCode,
            @RequestParam("type") String type) {
        UserSessionBo bo = getUserSession();
        PermissionAndTopNavBo user_permission_menu_topNav = imUserPermissionRbacService.getPermissionMenuTopNav( pathOrIndex, type, bo.getStaff_Id(), topNavCode);
        return ResponseEntity.ok().body(ResultUtil.OK(user_permission_menu_topNav));
    }

//    @SysLogAnnotion("sso账号验证接口")
    @RequestMapping(value = "/sso/validate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<JsonResultAo<SsoUserInfoVo>> ssoUserValidate(@RequestBody(required = false) SsoUserInfoVo vo) {
        SsoUserInfoVo ssoUserInfoVo = service.ssoUserValidate(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(ssoUserInfoVo));
    }

//    @SysLogAnnotion("获取菜单权限和操作权限数据")
//    // @ApiOperation(value = "获取菜单权限和操作权限数据")
//    @PostMapping("/userpermission")
//    @ResponseBody
//    public ResponseEntity<JsonResult<PermissionMenuOperationBo>> getUserPermission() {
//        UserSessionBo bo = getUserSession();
//        PermissionMenuOperationBo permissionMenuOperationBo = new PermissionMenuOperationBo();
//        permissionMenuOperationBo.setSession_id(bo.getSession_id());
//        permissionMenuOperationBo.setStaff_id(bo.getStaff_Id());
//        permissionMenuOperationBo.setTenant_id(bo.getTenant_Id());
//
//        /** 菜单权限数据 */
//        List<PermissionMenuBo> user_permission_menu = imUserPermissionService.getPermissionMenu(bo.getStaff_Id(), bo.getTenant_Id());
//        permissionMenuOperationBo.setUser_permission_menu(user_permission_menu);
//        /** 操作权限数据  */
//        List<PermissionOperationBo> user_permission_operation = imUserPermissionService.getPermissionOperation(bo.getStaff_Id(), bo.getTenant_Id());
//        permissionMenuOperationBo.setUser_permission_operation(user_permission_operation);
//
//        return ResponseEntity.ok().body(ResultUtil.OK(permissionMenuOperationBo));
//    }


    @SysLogAnnotion("获取当前用户密码是否过期")
    @PostMapping("/user_pwd_warning")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MUserVo>> getUserPwdWarning() {
        MUserVo mUserVo = service.getUserPwdWarning(SecurityUtil.getLoginUser_id());
        return ResponseEntity.ok().body(ResultUtil.OK(mUserVo));
    }
}
