package com.xinyirun.scm.ai.workflow.node.mailsend;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.bean.system.vo.mail.SendMailVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.service.mail.ISendMailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流邮件发送节点
 * 参考 aideepin: com.moyz.adi.common.workflow.node.mailsender.MailSendNode
 * 
 * 适配说明：
 * - 使用scm-ai的ISendMailService替代aideepin的AdiMailSender
 * - 使用config_code管理邮件配置，替代自定义SMTP
 * - 支持scm-ai的高级功能：bcc、single_send
 * 
 * @author SCM-AI团队
 * @since 2025-10-27
 */
@Slf4j
public class MailSendNode extends AbstractWfNode {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(\\S+)$");

    public MailSendNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo node,
                       WfState wfState, WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        // 1. 获取并验证配置
        // 参考 aideepin MailSendNode.java Line 36
        MailSendNodeConfig nodeConfig = checkAndGetConfig(MailSendNodeConfig.class);
        
        // 2. 渲染模板变量
        // 参考 aideepin MailSendNode.java Line 38-40
        String subject = WorkflowUtil.renderTemplate(nodeConfig.getSubject(), state.getInputs());
        String content = WorkflowUtil.renderTemplate(nodeConfig.getContent(), state.getInputs());
        String toMails = WorkflowUtil.renderTemplate(nodeConfig.getToMails(), state.getInputs());
        
        // 3. 验证收件人
        // 参考 aideepin MailSendNode.java Line 45-48
        List<String> validToMails = filterValidMails(toMails);
        if (validToMails.isEmpty()) {
            log.warn("邮件发送节点收件人为空, nodeUuid: {}", state.getUuid());
            throw new BusinessException("邮件收件人不能为空");
        }
        
        // 4. 构建SendMailVo参数（使用scm-ai的邮件服务）
        SendMailVo sendMailVo = new SendMailVo();
        sendMailVo.setConfig_code(nodeConfig.getConfigCode());
        sendMailVo.setSubject(subject);
        sendMailVo.setMessage(content);  // scm-ai用message字段
        sendMailVo.setTo(validToMails);
        sendMailVo.setSingle_send(nodeConfig.getSingleSend());
        
        // 5. 处理抄送
        // 参考 aideepin MailSendNode.java Line 50-54
        if (StringUtils.isNotBlank(nodeConfig.getCcMails())) {
            String ccMails = WorkflowUtil.renderTemplate(nodeConfig.getCcMails(), state.getInputs());
            List<String> validCcMails = filterValidMails(ccMails);
            if (!validCcMails.isEmpty()) {
                sendMailVo.setCc_list(validCcMails);
            }
        }
        
        // 6. 处理密送（scm-ai扩展功能）
        if (StringUtils.isNotBlank(nodeConfig.getBccMails())) {
            String bccMails = WorkflowUtil.renderTemplate(nodeConfig.getBccMails(), state.getInputs());
            List<String> validBccMails = filterValidMails(bccMails);
            if (!validBccMails.isEmpty()) {
                sendMailVo.setBcc_list(validBccMails);
            }
        }
        
        // 7. 发送邮件
        // 参考 aideepin MailSendNode.java Line 65-73
        ISendMailService sendMailService = SpringUtil.getBean(ISendMailService.class);
        sendMailService.send(sendMailVo);
        
        // 8. 返回结果
        // 参考 aideepin MailSendNode.java Line 75
        NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", "邮件发送成功");
        return NodeProcessResult.builder().content(List.of(output)).build();
    }
    
    /**
     * 过滤有效邮箱地址
     * 参考 aideepin MailSendNode.java Line 99-110: filterValidMails()
     * 
     * @param mails 邮箱地址字符串（逗号分隔）
     * @return 有效邮箱地址列表
     */
    private List<String> filterValidMails(String mails) {
        if (StringUtils.isBlank(mails)) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(mails.split(","))
                .map(String::trim)
                .filter(this::isValidEmail)
                .collect(Collectors.toList());
    }
    
    /**
     * 验证邮箱地址格式
     * 参考 aideepin MailSendNode.java Line 112-114: checkMail()
     * 
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        boolean valid = EMAIL_PATTERN.matcher(email).matches();
        if (!valid) {
            log.warn("邮箱地址无效，忽略: {}", email);
        }
        return valid;
    }
}
