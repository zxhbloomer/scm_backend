package com.xinyirun.scm.app.controller.master.user;

// import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
// import com.github.xiaoymin.knife4j.annotations.ApiSupport;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.master.user.AppMStaffVo;
import com.xinyirun.scm.bean.app.vo.master.user.AppPasswordVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserService;
import com.xinyirun.scm.core.app.service.master.user.AppIMStaffService;
import com.xinyirun.scm.framework.base.controller.app.v1.AppBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  app个人信息
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@RestController
// @ApiSupport(order = 6)
@RequestMapping(value = "/api/app/v1/user")
@Slf4j
// @Api(tags = "编辑个人信息")
public class AppMStaffController extends AppBaseController {

    @Autowired
    private AppIMStaffService service;

    @Autowired
    private AppIMUserService userService;

    // @ApiOperationSupport(order = 1)
    @SysLogAppAnnotion("获取个人信息接口")
    // @ApiOperation(value = "获取个人信息接口")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppMStaffVo>> getDetail() {
        AppMStaffVo entity = service.getDetail();
        return ResponseEntity.ok().body(AppResultUtil.OK(entity));
    }

    // @ApiOperationSupport(order = 4)
    @SysLogAppAnnotion("保存个人信息接口")
    // @ApiOperation(value = "保存个人信息接口")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
		public ResponseEntity<AppJsonResultAo<AppMStaffVo>> save(@RequestBody(required = false)AppMStaffVo bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(AppResultUtil.OK(service.selectById(bean.getStaff_id()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    // @ApiOperationSupport(order = 4)
    @SysLogAppAnnotion("修改密码接口")
    // @ApiOperation(value = "修改密码接口")
    @PostMapping("/password/update")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<String>> updatePassword(@RequestBody(required = false) AppPasswordVo vo) {
        vo.setEncode_pwd(super.getPassword(vo.getPwd()));
        vo.setEncode_pwd_his_pwd(super.getPassword(vo.getPwd_his_pwd()));
        if(userService.updatePwd(vo).isSuccess()){
            return ResponseEntity.ok().body(AppResultUtil.OK("OK"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAppAnnotion("app:头像上传")
    // @ApiOperation(value = "app:头像上传")
    @PostMapping("/avatar")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<String>> saveAvatar(String url) {
        service.saveAvatar(url);
        return ResponseEntity.ok().body(AppResultUtil.OK("OK"));
    }


}
