package com.xinyirun.scm.ai.workflow.node.mailsend;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮件发送节点配置
 * 参考 aideepin: com.moyz.adi.common.workflow.node.mailsender.MailSendNodeConfig
 * 
 * 适配scm-ai邮件系统：
 * - 使用config_code指定邮件配置（替代aideepin的SmtpInfo和SenderInfo）
 * - 使用scm-ai的ISendMailService服务
 * - 支持scm-ai的扩展功能：bcc、single_send
 */
@Data
public class MailSendNodeConfig {
    
    /**
     * 邮件配置编号（对应s_mail_config表的code字段）
     * scm-ai特有：替代aideepin的自定义SMTP配置
     */
    @NotBlank
    @JSONField(name = "config_code")
    private String configCode;
    
    /**
     * 收件人邮箱（多个用逗号分隔，支持模板变量）
     * 参考 aideepin: to_mails字段
     */
    @NotBlank
    @JSONField(name = "to_mails")
    private String toMails;
    
    /**
     * 抄送邮箱（多个用逗号分隔，支持模板变量）
     * 参考 aideepin: cc_mails字段
     */
    @JSONField(name = "cc_mails")
    private String ccMails;
    
    /**
     * 密送邮箱（多个用逗号分隔，支持模板变量）
     * scm-ai扩展：aideepin没有此功能
     */
    @JSONField(name = "bcc_mails")
    private String bccMails;
    
    /**
     * 邮件主题（支持模板变量）
     * 参考 aideepin: subject字段
     */
    @NotBlank
    private String subject;
    
    /**
     * 邮件内容（支持模板变量，HTML格式）
     * 参考 aideepin: content字段
     */
    @NotBlank
    private String content;
    
    /**
     * 是否单条发送（scm-ai扩展）
     * true: 每个收件人单独发送
     * false: 所有收件人一起发送
     */
    @JSONField(name = "single_send")
    private Boolean singleSend = true;
}
