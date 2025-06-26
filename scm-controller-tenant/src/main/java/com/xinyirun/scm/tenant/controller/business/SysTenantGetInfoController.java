package com.xinyirun.scm.tenant.controller.business;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.SLoginUserBo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.tenant.service.business.login.ISLoginUserService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 租户信息获取 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenant")
public class SysTenantGetInfoController extends SystemBaseController {    @Autowired
    private ISLoginUserService loginUserService;
    
    /**
     * 获取租户信息
     *
     * @param bo 登录用户参数
     * @return 租户用户列表
     */    @PostMapping("/get")
    @ResponseBody
    @SysLogAnnotion("获取租户信息")
    public ResponseEntity<JsonResultAo<List<SLoginUserBo>>> getTenantInfo(@RequestBody(required = false) SLoginUserBo bo) {
        List<SLoginUserBo> list = loginUserService.getTenant(bo);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
