package com.xinyirun.scm.bean.system.vo.mail;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 发送邮件参数
 * @CreateTime : 2023/12/12 17:11
 */

@Data
public class SendMailVo implements Serializable {

    
    private static final long serialVersionUID = -6411482391584579077L;

    /**
     * 配置编号
     */
    private String config_code;

    /**
     * 主标题
     */
    private String subject;

    /**
     * 消息
     */
    private String message;

    /**
     * 接收人
     */
    private List<String> to;

    /**
     * 抄送人
     */
    private List<String> cc_list;

    /**
     * 密送
     */
    private List<String> bcc_list;

    /**
     * 是否单条发送, 默认true
     */
    private Boolean single_send = true;

    /**
     * 是否包含附件
     */
    private boolean send_attachment;

    /**
     * 附件列表
     */
//    private List<MailFileParam> file_list;
}
