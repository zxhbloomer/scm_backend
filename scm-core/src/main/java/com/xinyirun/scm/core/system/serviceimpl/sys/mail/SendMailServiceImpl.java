package com.xinyirun.scm.core.system.serviceimpl.sys.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Sets;
import com.xinyirun.scm.bean.entity.sys.mail.SMailConfigEntity;
import com.xinyirun.scm.bean.system.vo.mail.SendMailVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.sys.mail.SMailConfigMapper;
import com.xinyirun.scm.core.system.service.mail.ISMailConfigService;
import com.xinyirun.scm.core.system.service.mail.ISendMailService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/12/12 17:46
 */

@Service
public class SendMailServiceImpl extends BaseServiceImpl<SMailConfigMapper, SMailConfigEntity> implements ISendMailService {

    @Autowired
    private ISMailConfigService mailConfigService;

    private static final String MIME_TYPE_DEFAULT = "text/html; charset=utf-8";

    /**
     * 发送邮件
     *
     * @param param
     */
    @Override
    public void send(SendMailVo param) {
        if (StringUtils.isBlank(param.getConfig_code())) {
            throw new BusinessException("请选择配置");
        }
        // 查询配置
        SMailConfigEntity sMailConfigEntity = mailConfigService.selectByCode(param.getConfig_code());
        if (sMailConfigEntity == null) {
            throw new BusinessException("配置不存在");
        }
        // 获取邮件发件器
        JavaMailSenderImpl mailSender = getMailSender(sMailConfigEntity);
        if (CollectionUtils.isEmpty(param.getTo())) {
            return;
        }
        // 获取to cc bcc 中所有允许发送的receive
        HashSet<String> allReceivers = Sets.newHashSet(param.getTo());
        // 密送
        if (!CollectionUtils.isEmpty(param.getBcc_list())) {
            allReceivers.addAll(param.getBcc_list());
        }
        // 抄送
        if (!CollectionUtils.isEmpty(param.getCc_list())) {
            allReceivers.addAll(param.getCc_list());
        }
        // 设置接收人, 过滤重复的
        Set<String> receivers = Sets.intersection(allReceivers, new HashSet<>(param.getTo()));
        if (CollUtil.isEmpty(receivers)) {
            return;
        }
        List<MimeMessage> mimeMessageList = new ArrayList<>();

        // 是否单条发送
        if (param.getSingle_send()) {
            receivers.forEach(item -> {
                try {
                    buildMailParam(param, sMailConfigEntity, mailSender, mimeMessageList, allReceivers, item);
                } catch (MessagingException e) {
                    throw new BusinessException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new BusinessException(e);
                }
            });
        } else {
            try {
                this.buildMailParam(param, sMailConfigEntity, mailSender, mimeMessageList, allReceivers,
                        ArrayUtil.toArray(receivers, String.class));
            } catch (MessagingException e) {
                throw new BusinessException(e);
            } catch (UnsupportedEncodingException e) {
                throw new BusinessException(e);
            }
        }
        // 调用发送
        mailSender.send(ArrayUtil.toArray(mimeMessageList, MimeMessage.class));

    }


    private void buildMailParam(SendMailVo param, SMailConfigEntity mailConfig, JavaMailSenderImpl mailSender
            , List<MimeMessage> mimeMessageList, HashSet<String> allReceivers, String... to) throws MessagingException
            , UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(new InternetAddress(mailConfig.getFrom_(), mailConfig.getSender()));
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(param.getSubject());

        // 处理抄送
        List<String> ccList = param.getCc_list();
        if (CollUtil.isNotEmpty(ccList)) {
            Set<String> ccReceivers = Sets.intersection(allReceivers, Sets.newHashSet(ccList));
            if (!CollectionUtil.isEmpty(ccReceivers)) {
                String[] ccReceiverArray = new String[ccReceivers.size()];
                ccReceivers.toArray(ccReceiverArray);
                mimeMessageHelper.setCc(ccReceiverArray);
            }
        }

        // 处理密送
        List<String> bccList = param.getBcc_list();
        if (CollUtil.isNotEmpty(bccList)) {
            Set<String> bccReceivers = Sets.intersection(allReceivers, Sets.newHashSet(bccList));
            if (!CollectionUtil.isEmpty(bccReceivers)) {
                String[] bccReceiverArray = new String[bccReceivers.size()];
                bccReceivers.toArray(bccReceiverArray);
                mimeMessageHelper.setBcc(ArrayUtil.toArray(bccList, String.class));
            }
        }

        // 创建一个消息部分来代表正文
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(param.getMessage(), MIME_TYPE_DEFAULT);
        // 使用MimeMultipart，因为我们需要处理文件附件
        Multipart multipart = new MimeMultipart();

        // 判断是否包含附件
//        if (param.isSend_attachment()) {
//            this.buildAttachmentParam(mailParam, multipart);
//        }

        // 添加正文
        multipart.addBodyPart(messageBodyPart);
        // 将所有消息部分放入消息中
        mimeMessage.setContent(multipart);
        mimeMessageList.add(mimeMessage);
    }

    /**
     * 获取邮件发件器
     * @param entity
     */
    private JavaMailSenderImpl getMailSender(SMailConfigEntity entity) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(entity.getHost());
        mailSender.setPort(entity.getPort());
        mailSender.setUsername(entity.getUsername());
        mailSender.setPassword(entity.getPassword());

        Properties props = new Properties();
        // 判断是否是TLS
        if (Objects.equals(Optional.ofNullable(entity.getSecurity_type()).orElse(0), 2)) {
            props.setProperty("mail.smtp.starttls.enable", "true");
        } else if (Objects.equals(Optional.ofNullable(entity.getSecurity_type()).orElse(0), 3)) {
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.socketFactory.port", entity.getPort() + "");
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.debug", "true");
        }
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }
}
