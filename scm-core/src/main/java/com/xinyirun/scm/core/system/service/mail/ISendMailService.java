package com.xinyirun.scm.core.system.service.mail;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.mail.SMailConfigEntity;
import com.xinyirun.scm.bean.system.vo.mail.SendMailVo;

public interface ISendMailService extends IService<SMailConfigEntity> {

    /**
     * 发送邮件
     * @param param
     */
    void send(SendMailVo param);
}
