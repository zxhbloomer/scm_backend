package com.xinyirun.scm.app.controller.logout;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.app.service.master.user.jwt.AppIMUserJwtTokenService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  登出
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@Slf4j
// @Api(tags = "登出")
@RestController
@RequestMapping(value = "/api/app/v1/logout")
public class AppLogOutController extends SystemBaseController {

    @Autowired
    private AppIMUserJwtTokenService service;

    @SysLogAppAnnotion("登出逻辑")
    // @ApiOperation(value = "登出逻辑")
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<String>> doLogout() {
        if(!service.logOut().isSuccess()){
            throw new UpdateErrorException("登出逻辑发生异常。");
        }
        return ResponseEntity.ok().body(AppResultUtil.OK("OK"));
    }
}
