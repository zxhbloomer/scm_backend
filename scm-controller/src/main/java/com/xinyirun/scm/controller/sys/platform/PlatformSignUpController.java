package com.xinyirun.scm.controller.sys.platform;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.platform.SignUpVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.platform.ISignUpService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: SignupController
 * @Description: 注册用户
 * @Author: zxh
 * @date: 2019/12/16
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/signup")
@Slf4j
// @Api(tags = "自主注册用户")
public class PlatformSignUpController extends SystemBaseController {

    @Autowired
    ISignUpService service;

    @SysLogAnnotion("注册根据手机号码，租户名称，管理员，密码，生成注册信息")
    // @ApiOperation(value = "注册根据手机号码，租户名称，管理员，密码，生成注册信息")
    @PostMapping("/mobile")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> signUp(@RequestBody(required = false) SignUpVo bean) {
        // 1:check
        if(service.check(bean)) {
            bean.setEncodePassword(getPassword(bean.getPassword()));
            // 2:执行注册
            service.signUp(bean);
            // 3:启动租户的定时任务
//            tenantMqProducter.mqSendAfterDataSave(bean.getTenant_id());
        }
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("check手机号码是否已经被使用")
    // @ApiOperation(value = "check手机号码是否已经被使用")
    @PostMapping("/check/mobile")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> checkMobile(@RequestBody(required = false) SignUpVo bean) {
        // 1:check
        service.checkMobile(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
