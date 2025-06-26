package com.xinyirun.scm.controller.code.sms;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.SSmsCodeVo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.bean.system.vo.sys.platform.SignUpVo;
import com.xinyirun.scm.common.annotations.LimitAnnotion;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.security.code.ValidateCode;
import com.xinyirun.scm.security.code.ValidateCodeGenerator;
import com.xinyirun.scm.security.code.sms.SmsCodeSender;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 短信验证码
 *
 * @author
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
// @Api(tags = "短信验证码")
public class SysSmsCodeController extends SystemBaseController {

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Autowired
    private ValidateCodeGenerator smsCodeGenerator;

    @Autowired
    private SmsCodeSender smsCodeSender;

    @Autowired
    private IMUserService imUserService;

    /**
     * 测试限流注解，下面配置说明该接口 60秒内最多只能访问 10次，保存到 redis的键名为 limit_test，
     * 即 prefix + "_" + key，也可以根据 IP 来限流，需指定 limitType = LimitType.IP
     */
    @SysLogAnnotion("短信发送")
    // @ApiOperation(value = "短信发送")
    @LimitAnnotion(key = "smscode", period = 60, count = 5, name = "短信验证码", prefix = "limit")
    @PostMapping("/sms/code")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> createSmsCode(HttpServletRequest request, HttpServletResponse response,
                                                              @RequestBody SSmsCodeVo mobileBean) throws Exception {
        // check 手机号码是否已经被注册
        MUserVo mUserVO = imUserService.getDataByName(mobileBean.getMobile());
        if (mUserVO != null) {
            throw new BusinessException("该手机号码已经被注册使用，您可以使用该手机号码进行登录！");
        }

        ValidateCode smsCode = smsCodeGenerator.createCode();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SystemConstants.SESSION_KEY_SMS_CODE + mobileBean.getMobile(), smsCode);
        // 发送短信
        smsCodeSender.sendCode(mobileBean.getMobile(), smsCode.getCode(), SystemConstants.SMS_CODE_TYPE.SMS_CODE_TYPE_REGIST);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("短信验证")
    // @ApiOperation(value = "短信验证")
    @LimitAnnotion(key = "smscodecheck", period = 60, count = 5, name = "验证短信验证码", prefix = "limit")
    @PostMapping("/sms/code/check")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkSmsCode(HttpServletRequest request, HttpServletResponse response,
                                                             @RequestBody(required=false) SSmsCodeVo mobileBean)  {
        // 本方法主要是触发框架短信验证 SmsCodeFilter（框架自动验证），并且验证手机号码是否可用
        SignUpVo bean = new SignUpVo();
        bean.setMobile(mobileBean.getMobile());
//        service.checkMobile(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}