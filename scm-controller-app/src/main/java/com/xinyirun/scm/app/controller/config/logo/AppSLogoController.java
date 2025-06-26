package com.xinyirun.scm.app.controller.config.logo;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.sys.config.AppLogoVo;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.core.app.service.sys.config.AppISAppLogoService;
import com.xinyirun.scm.framework.base.controller.app.v1.AppBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  app logo
 * </p>
 *
 * @author wwl
 * @since 2022-02-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/app/v1/logo")
public class AppSLogoController extends AppBaseController {

    @Autowired
    private AppISAppLogoService service;

    @SysLogAppAnnotion("获取logo")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppLogoVo>> get() {
        AppLogoVo result = service.get();
        return ResponseEntity.ok().body(AppResultUtil.OK(result));
    }


}
