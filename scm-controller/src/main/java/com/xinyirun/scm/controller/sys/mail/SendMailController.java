package com.xinyirun.scm.controller.sys.mail;

import com.xinyirun.scm.bean.system.vo.mail.SendMailVo;
import com.xinyirun.scm.core.system.service.mail.ISendMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Wqf
 * @Description: 发送邮件
 * @CreateTime : 2023/12/12 16:59
 */

@RestController
@RequestMapping(value = "/api1/v1/mail/send")
@Slf4j
public class SendMailController {

    @Autowired
    private ISendMailService service;

    @PostMapping
    public String send(@RequestBody SendMailVo param) {
        service.send(param);
        return "ok";
    }
}
