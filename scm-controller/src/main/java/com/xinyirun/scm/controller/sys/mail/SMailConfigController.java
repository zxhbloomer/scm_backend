package com.xinyirun.scm.controller.sys.mail;

import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.mail.SMailConfigVo;
import com.xinyirun.scm.core.system.service.mail.ISMailConfigService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Wqf
 * @Description: 邮件配置 前端控制器
 * @CreateTime : 2023/12/12 11:28
 */

@RestController
@RequestMapping(value = "/api/v1/mail")
@Slf4j
public class SMailConfigController extends SystemBaseController {

    @Autowired
    private ISMailConfigService service;

    @PostMapping("/insert")
    public void insert(@RequestBody SMailConfigVo vo) {
        // 密码加密
        InsertResultAo<SMailConfigVo> result = service.insert(vo);
    }


}
