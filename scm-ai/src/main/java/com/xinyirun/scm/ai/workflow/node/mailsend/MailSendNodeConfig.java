package com.xinyirun.scm.ai.workflow.node.mailsend;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮件发送节点配置
 *
 * 适配scm-ai邮件系统：
 * - 使用config_code指定邮件配置
 * - 使用scm-ai的ISendMailService服务
 * - 支持scm-ai的扩展功能：bcc、single_send
 */
@Data
public class MailSendNodeConfig {
    
    /**
     * 邮件配置编号（对应s_mail_config表的code字段）
     * 使用系统预配置的邮件服务器配置
     */
    @NotBlank
    @JSONField(name = "config_code")
    private String configCode;
    
    /**
     * 收件人邮箱（多个用逗号分隔,支持模板变量）
     */
    @NotBlank
    @JSONField(name = "to_mails")
    private String toMails;

    /**
     * 抄送邮箱（多个用逗号分隔,支持模板变量）
     */
    @JSONField(name = "cc_mails")
    private String ccMails;
    
    /**
     * 密送邮箱（多个用逗号分隔，支持模板变量）
     * 支持密送功能，对其他收件人不可见
     */
    @JSONField(name = "bcc_mails")
    private String bccMails;
    
    /**
     * 邮件主题（支持模板变量）
     */
    @NotBlank
    private String subject;

    /**
     * 邮件内容（支持模板变量,HTML格式）
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
