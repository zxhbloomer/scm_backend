package com.xinyirun.scm.core.bpm.serviceimpl.business.notice;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.xinyirun.scm.bean.bpm.vo.BpmInstanceVo;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceApproveEntity;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceEntity;
import com.xinyirun.scm.bean.entity.business.notice.BNoticeEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCommentVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmNoticeVo;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import com.xinyirun.scm.core.bpm.mapper.business.BpmInstanceApproveMapper;
import com.xinyirun.scm.core.bpm.mapper.business.BpmInstanceMapper;
import com.xinyirun.scm.core.bpm.mapper.business.notice.BpmNoticeMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.service.business.notice.IBpmBNoticeService;
import com.xinyirun.scm.core.bpm.service.business.notice.IBpmBNoticeStaffService;
import com.xinyirun.scm.core.bpm.service.common.config.IBpmSConfigService;
import com.xinyirun.scm.core.bpm.service.common.staff.IBpmStaffService;
import com.xinyirun.scm.core.bpm.serviceimpl.base.v1.BpmBaseServiceImpl;
import com.xinyirun.scm.core.bpm.utils.bpm.MarkDownNotice;
import com.xinyirun.scm.core.bpm.utils.websocket.BpmWebSocket2Utils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Service
public class BpmSNoticeServiceImpl extends BpmBaseServiceImpl<BpmNoticeMapper, BNoticeEntity> implements IBpmBNoticeService {

    @Autowired
    @Lazy
    private RuntimeService runtimeService;

    @Autowired
    private BpmInstanceMapper mapper;

    @Autowired
    private IBpmSConfigService configService;

    @Autowired
    private BpmNoticeMapper noticeMapper;

    @Autowired
    private IBpmBNoticeStaffService noticeStaffService;

    @Autowired
    private BpmInstanceApproveMapper bpmInstanceApproveMapper;

    @Autowired
    private IBpmStaffService staffService;

    @Autowired
    private BpmWebSocket2Utils bpmWebSocket2Utils;

    @Autowired
    @Lazy
    private HistoryService historyService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    /**
     * 发送审批通知：
     * @param task
     */
    @Override
    public void sendBpmTodoNotice(TaskEntity task){
        // 1.判断流程实例是否结束
        boolean isRunning = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).count() > 0;
        Object variable = null;
        if (isRunning){
            // 未结束 获取任务变量
            variable= runtimeService.getVariable(task.getProcessInstanceId(), WflowGlobalVarDef.APPROVE+task.getId());
        }else {
            // 流程结束 获取任务历史变量（流程结束，runtimeService 清除变量）
            variable = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .variableName(WflowGlobalVarDef.APPROVE + task.getId())
                    .singleResult().getValue();
        }

        // 2.判断任务同意/拒绝(发起) 多人
        if (variable != null && variable.equals(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE)) {
//            sendBpmTodoRefuseNotice(task);
        } else {
            // 审批通过，消息通知：下一个审批者，单人
            sendBpmTodoAgreeNextApprovePersonNotice(task);
            // 审批通过，消息通知：多人
            sendBpmAgreeNotice(task);
        }
    }

    /**
     * 审批流通过通知
     * @param processInstance
     */
    @Override
    public void sendBpmPassNotice(ProcessInstance processInstance){
        /**
         * 获取：
         * 1、审批流名称
         * 2、申请人
         * 3、当前进度：已通过部门经理审批 → 待您审批（财务审核）
         * 4、审批内容
         */
        BpmInstanceVo bpmInstanceVo = mapper.getInstanceAndSummary(processInstance.getProcessInstanceId(), processInstance.getProcessDefinitionId());
        BBpmNoticeVo noticeVo = new BBpmNoticeVo();

//        1、审批流名称
        noticeVo.setProcess_definition_name(bpmInstanceVo.getProcess_definition_name());
        noticeVo.setTitle("【审批任务更新通知-" + noticeVo.getProcess_definition_name()  + "-审批已通过】");

        // 2、申请人 提交时间
        noticeVo.setOwner_code(bpmInstanceVo.getOwner_code());
        noticeVo.setOwner_name(bpmInstanceVo.getOwner_name());
        noticeVo.setStart_time(bpmInstanceVo.getStart_time());

//        3、获取下一个审批人
//        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
//        if (StringUtils.isNotEmpty(bpmInstanceApproveEntity.getIs_next())) {
//            // 查询下一个节点审批人
//            BBpmInstanceVo _vo =  bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApproveEntity.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
//            String staff_code = _vo.getNext_approve_code();
//            noticeVo.setNext_staff_code(staff_code);
//            noticeVo.setNext_staff_name(_vo.getNext_approve_name());
//        }


//        4、获取摘要
        noticeVo.setSummary(bpmInstanceVo.getSummary());
//        5、获取审批流程：如果为空说明还在第一个节点
        List<BBpmCommentVo> bpmCommentVos = mapper.getProcessComments(bpmInstanceVo.getProcess_code());
        noticeVo.setComment(bpmCommentVos);

//        // 6、编辑跳转链接
//        noticeVo.setSerial_type(bpmInstanceVo.getSerial_type());
//        noticeVo.setSerial_id(bpmInstanceVo.getSerial_id());
//        String domain = configService.selectByKey(SystemConstants.SCM_SYSTEM_DOMAIN).getValue();
//        noticeVo.setApprovalUrl(domain+"/todo/index?process_code="+bpmInstanceVo.getProcess_code());
//        noticeVo.setDeadLine(LocalDateTime.now().plusDays(1));
        noticeVo.setC_time(LocalDateTime.now());


//        7、生成markdown文字
        Document doc = MarkDownNotice.createAgreeNotice(noticeVo);
        MutableDataSet options = new MutableDataSet();
        String str = Formatter.builder(options).build().render(doc);
        log.debug("生成的markdown文字:" + str);
        noticeVo.setMarkDown(str);

        // 生成html文字
        String htmlContent = "";
        // 初始化配置（支持表格、自动链接等扩展）
        MutableDataSet html_options = new MutableDataSet();
        html_options.set(Parser.EXTENSIONS,  Arrays.asList(
                TablesExtension.create(),    // 启用表格支持
                AutolinkExtension.create()   // 启用自动链接检测
        ));
        // 创建解析器和渲染器
        Parser parser = Parser.builder(html_options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(html_options).build();
        // 执行转换
        Node document = parser.parse(str);
        htmlContent = renderer.render(document);
        log.debug("生成的html文字:" + htmlContent);
        noticeVo.setHtml(htmlContent);

        // 8、插入到通知表中
        BNoticeVo entity = new BNoticeVo();
        // 0:系统通知
        entity.setType("1");
        entity.setTitle(noticeVo.getTitle());
        entity.setMsg(str);
        entity.setHtml(htmlContent);
        // 1:已发布
        entity.setStatus("1");
        /**
         * 获取要发送消息的用户
         */
        List<MStaffVo> sendStaffs = new ArrayList<>();
        for (BBpmCommentVo comment : bpmCommentVos) {
            String assignee = comment.getAssignee_code();
            MStaffVo vo = staffService.selectByCode(assignee);
            sendStaffs.add(vo);
        }

        entity.setStaff_list(sendStaffs);
        // 设置被影响的用户id
        InsertResultAo<BNoticeVo> vo = insert(entity);

        // 9、发送消息 websocket
        // 10:消息通知
        noticeVo.setType("0");
        bpmWebSocket2Utils.convertAndSendUser(sendStaffs, WebSocketConstants.WEBSOCKET_BPM_APPROVE_NOTICE, noticeVo);
    }

    /**
     * 审批流撤销\拒绝消息通知
     * @param processInstance
     */
    @Override
    public void sendBpTerminateNotice(ProcessInstance processInstance ){
        // 1.判断流程实例是否结束
        boolean isRunning = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getProcessInstanceId()).count() > 0;
        Object variable = null;
        if (isRunning){
            // 未结束 获取任务变量
            variable= runtimeService.getVariable(processInstance.getProcessInstanceId(), WflowGlobalVarDef.APPROVE+processInstance.getId());
        }else {
            // 流程结束 获取任务历史变量（流程结束，runtimeService 清除变量）
            List list = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.getProcessInstanceId()).list();

            variable = list.get(list.size() -1 );
        }

        if (variable != null && ((HistoricVariableInstanceEntityImpl) variable).getTextValue().equals(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE)) {
            // 拒绝 refuse
            sendBpmRefuseNotice(processInstance);
        } else {
            // 撤销 cancel
            sendBpmCancelNotice(processInstance);
        }
    }

    /**
     * 审批流撤销消息通知：发送多人
     * @param processInstance
     */
    public void sendBpmCancelNotice(ProcessInstance processInstance) {
        // 审批通过，消息通知
        /**
         * 获取：
         * 1、审批流名称
         * 2、申请人
         * 3、当前进度：已通过部门经理审批 → 待您审批（财务审核）
         * 4、审批内容
         */
        BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(processInstance.getProcessInstanceId(), processInstance.getProcessDefinitionId());
        BBpmNoticeVo noticeVo = new BBpmNoticeVo();

//        1、审批流名称
        noticeVo.setProcess_definition_name(bpmInstanceEntity.getProcess_definition_name());
        noticeVo.setTitle("【审批撤销通知-" + noticeVo.getProcess_definition_name()  + "】");

        // 2、申请人 提交时间
        noticeVo.setOwner_code(bpmInstanceEntity.getOwner_code());
        noticeVo.setOwner_name(bpmInstanceEntity.getOwner_name());
        noticeVo.setStart_time(bpmInstanceEntity.getStart_time());

//        3、获取下一个审批人
//        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
//        if (StringUtils.isNotEmpty(bpmInstanceApproveEntity.getIs_next())) {
//            // 查询下一个节点审批人
//            BBpmInstanceVo _vo =  bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApproveEntity.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
//            String staff_code = _vo.getNext_approve_code();
//            noticeVo.setNext_staff_code(staff_code);
//            noticeVo.setNext_staff_name(_vo.getNext_approve_name());
//        }


//        4、获取摘要
        noticeVo.setSummary(iBpmInstanceSummaryService.getDataByInstanceCode(bpmInstanceEntity.getProcess_code()).getSummary());
//        5、获取审批流程：如果为空说明还在第一个节点
        List<BBpmCommentVo> bpmCommentVos = mapper.getProcessComments(bpmInstanceEntity.getProcess_code());
        noticeVo.setComment(bpmCommentVos);

        // 6、编辑跳转链接
        noticeVo.setSerial_type(bpmInstanceEntity.getSerial_type());
        noticeVo.setSerial_id(bpmInstanceEntity.getSerial_id());
        String domain = configService.selectByKey(SystemConstants.SCM_SYSTEM_DOMAIN).getValue();
        noticeVo.setApprovalUrl(domain+"/todo/index?process_code="+bpmInstanceEntity.getProcess_code());
        noticeVo.setDeadLine(LocalDateTime.now().plusDays(1));
        noticeVo.setC_time(LocalDateTime.now());


//        7、生成markdown文字
        Document doc = MarkDownNotice.createBpmCanceNotice(noticeVo);
        MutableDataSet options = new MutableDataSet();
        String str = Formatter.builder(options).build().render(doc);
        log.debug("生成的markdown文字:" + str);
        noticeVo.setMarkDown(str);

        // 生成html文字
        String htmlContent = "";
        // 初始化配置（支持表格、自动链接等扩展）
        MutableDataSet html_options = new MutableDataSet();
        html_options.set(Parser.EXTENSIONS,  Arrays.asList(
                TablesExtension.create(),    // 启用表格支持
                AutolinkExtension.create()   // 启用自动链接检测
        ));
        // 创建解析器和渲染器
        Parser parser = Parser.builder(html_options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(html_options).build();
        // 执行转换
        Node document = parser.parse(str);
        htmlContent = renderer.render(document);
        log.debug("生成的html文字:" + htmlContent);
        noticeVo.setHtml(htmlContent);

        // 8、插入到通知表中
        BNoticeVo entity = new BNoticeVo();
        // 0:系统通知
        entity.setType("1");
        entity.setTitle(noticeVo.getTitle());
        entity.setMsg(str);
        entity.setHtml(htmlContent);
        // 1:已发布
        entity.setStatus("1");
        /**
         * 获取要发送消息的用户
         */
        List<MStaffVo> sendStaffs = new ArrayList<>();
        for (BBpmCommentVo comment : bpmCommentVos) {
            String assignee = comment.getAssignee_code();
            MStaffVo vo = staffService.selectByCode(assignee);
            sendStaffs.add(vo);
        }

        entity.setStaff_list(sendStaffs);
        // 设置被影响的用户id
        InsertResultAo<BNoticeVo> vo = insert(entity);

        // 9、发送消息 websocket
        // 10:消息通知
        noticeVo.setType("0");
        bpmWebSocket2Utils.convertAndSendUser(sendStaffs, WebSocketConstants.WEBSOCKET_BPM_CANCEL_NOTICE, noticeVo);
    }

    /**
     * 同意审批通知
     * 当前节点任务完成通知：
     * 通知发起下一个审批节点人员发起通知
     * 内容：
     * 【审批任务通知】
     * *标题*：2025年市场活动预算申请
     * *申请人*：张三（市场部）
     * *当前进度*：已通过部门经理审批 → 待您审批（财务审核）
     * *说明*：请核对费用明细，审批截止时间：2025-02-23 18:00
     * *操作*：点击查看详情 [审批链接]
     *
     * 这是一个系统发送的通知，发送给下一个审批节点的审批人员
     *
     */
    public void sendBpmTodoAgreeNextApprovePersonNotice(TaskEntity task){
        /**
         * 获取：
         * 1、审批流名称
         * 2、申请人
         * 3、当前进度：已通过部门经理审批 → 待您审批（财务审核）
         * 4、审批内容
         */
        BpmInstanceVo bpmInstanceVo = mapper.getInstanceAndSummary(task.getProcessInstanceId(), task.getProcessDefinitionId());
        BBpmNoticeVo noticeVo = new BBpmNoticeVo();

//        1、审批流名称
        noticeVo.setProcess_definition_name(bpmInstanceVo.getProcess_definition_name());
        noticeVo.setTitle("【审批任务通知-" + noticeVo.getProcess_definition_name()  + "】");

        // 2、申请人 提交时间
        noticeVo.setOwner_code(bpmInstanceVo.getOwner_code());
        noticeVo.setOwner_name(bpmInstanceVo.getOwner_name());
        noticeVo.setStart_time(bpmInstanceVo.getStart_time());

//        3、获取下一个审批人
        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
        if (StringUtils.isNotEmpty(bpmInstanceApproveEntity.getIs_next())) {
            // 查询下一个节点审批人
            BBpmInstanceVo _vo =  bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApproveEntity.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
            if(_vo != null) {
                String staff_code = _vo.getNext_approve_code();
                noticeVo.setNext_staff_code(staff_code);
                noticeVo.setNext_staff_name(_vo.getNext_approve_name());
            }
        }


//        4、获取摘要
        noticeVo.setSummary(bpmInstanceVo.getSummary());
//        5、获取审批流程：如果为空说明还在第一个节点
        List<BBpmCommentVo> bpmCommentVos = mapper.getProcessComments(bpmInstanceVo.getProcess_code());
        noticeVo.setComment(bpmCommentVos);

        // 6、编辑跳转链接
        noticeVo.setSerial_type(bpmInstanceVo.getSerial_type());
        noticeVo.setSerial_id(bpmInstanceVo.getSerial_id());
        String domain = configService.selectByKey(SystemConstants.SCM_SYSTEM_DOMAIN).getValue();
        noticeVo.setApprovalUrl(domain+"/todo/index?process_code="+bpmInstanceVo.getProcess_code());
        noticeVo.setDeadLine(LocalDateTime.now().plusDays(1));
        noticeVo.setC_time(LocalDateTime.now());


//        7、生成markdown文字
        Document doc = MarkDownNotice.createTodoAgreeNextApprovePersonNotice(noticeVo);
        MutableDataSet options = new MutableDataSet();
        String str = Formatter.builder(options).build().render(doc);
        log.debug("生成的markdown文字:" + str);
        noticeVo.setMarkDown(str);

        // 生成html文字
        String htmlContent = "";
        // 初始化配置（支持表格、自动链接等扩展）
        MutableDataSet html_options = new MutableDataSet();
        html_options.set(Parser.EXTENSIONS,  Arrays.asList(
                TablesExtension.create(),    // 启用表格支持
                AutolinkExtension.create()   // 启用自动链接检测
        ));
        // 创建解析器和渲染器
        Parser parser = Parser.builder(html_options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(html_options).build();
        // 执行转换
        Node document = parser.parse(str);
        htmlContent = renderer.render(document);
        log.debug("生成的html文字:" + htmlContent);
        noticeVo.setHtml(htmlContent);

        // 8、插入到通知表中
        BNoticeVo entity = new BNoticeVo();
        // 0:系统通知
        entity.setType("1");
        entity.setTitle(noticeVo.getTitle());
        entity.setMsg(str);
        entity.setHtml(htmlContent);
        // 1:已发布
        entity.setStatus("1");
        // noticeVo.getNext_staff_code()按都好，split进行循环查询
        String nextStaffCodes = noticeVo.getNext_staff_code();
        List<MStaffVo> sendStaffs = new ArrayList<>();
        if (nextStaffCodes != null && !nextStaffCodes.isEmpty())  {
            for (String staffCode : nextStaffCodes.split(","))  {
                // 添加自定义逻辑（示例）
                String trimmedCode = staffCode.trim();
                MStaffVo vo = staffService.selectByCode(trimmedCode);
                sendStaffs.add(vo);
            }
        }

        entity.setStaff_list(sendStaffs);
        // 设置被影响的用户id
        InsertResultAo<BNoticeVo> vo = insert(entity);

        // 9、发送消息 websocket
        // 10:消息通知
        noticeVo.setType("0");
        bpmWebSocket2Utils.convertAndSendUser(sendStaffs, WebSocketConstants.WEBSOCKET_BPM_APPROVE_NOTICE, noticeVo);
    }

    /**
     * 审批通过，消息通知：多人
     * 当前节点任务完成通知：
     * 通知发起下一个审批节点人员发起通知
     * 内容：
     * 【审批任务通知】
     * *标题*：2025年市场活动预算申请
     * *申请人*：张三（市场部）
     * *当前进度*：已通过部门经理审批 → 待您审批（财务审核）
     * *说明*：请核对费用明细，审批截止时间：2025-02-23 18:00
     * *操作*：点击查看详情 [审批链接]
     *
     * 这是一个系统发送的通知，发送给所有已经审批通过的人员
     *
     */
    public void sendBpmAgreeNotice(TaskEntity task){
        /**
         * 获取：
         * 1、审批流名称
         * 2、申请人
         * 3、当前进度：已通过部门经理审批 → 待您审批（财务审核）
         * 4、审批内容
         */
        BpmInstanceVo bpmInstanceVo = mapper.getInstanceAndSummary(task.getProcessInstanceId(), task.getProcessDefinitionId());
        BBpmNoticeVo noticeVo = new BBpmNoticeVo();

//        1、审批流名称
        noticeVo.setProcess_definition_name(bpmInstanceVo.getProcess_definition_name());
        noticeVo.setTitle("【审批任务更新通知-" + noticeVo.getProcess_definition_name()  + "】");

        // 2、申请人 提交时间
        noticeVo.setOwner_code(bpmInstanceVo.getOwner_code());
        noticeVo.setOwner_name(bpmInstanceVo.getOwner_name());
        noticeVo.setStart_time(bpmInstanceVo.getStart_time());

//        3、获取下一个审批人
//        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
//        if (StringUtils.isNotEmpty(bpmInstanceApproveEntity.getIs_next())) {
//            // 查询下一个节点审批人
//            BBpmInstanceVo _vo =  bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApproveEntity.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
//            String staff_code = _vo.getNext_approve_code();
//            noticeVo.setNext_staff_code(staff_code);
//            noticeVo.setNext_staff_name(_vo.getNext_approve_name());
//        }


//        4、获取摘要
        noticeVo.setSummary(bpmInstanceVo.getSummary());
//        5、获取审批流程：如果为空说明还在第一个节点
        List<BBpmCommentVo> bpmCommentVos = mapper.getProcessComments(bpmInstanceVo.getProcess_code());
        noticeVo.setComment(bpmCommentVos);

//        // 6、编辑跳转链接
//        noticeVo.setSerial_type(bpmInstanceVo.getSerial_type());
//        noticeVo.setSerial_id(bpmInstanceVo.getSerial_id());
//        String domain = configService.selectByKey(SystemConstants.SCM_SYSTEM_DOMAIN).getValue();
//        noticeVo.setApprovalUrl(domain+"/todo/index?process_code="+bpmInstanceVo.getProcess_code());
//        noticeVo.setDeadLine(LocalDateTime.now().plusDays(1));
        noticeVo.setC_time(LocalDateTime.now());


//        7、生成markdown文字
        Document doc = MarkDownNotice.createAgreeNotice(noticeVo);
        MutableDataSet options = new MutableDataSet();
        String str = Formatter.builder(options).build().render(doc);
        log.debug("生成的markdown文字:" + str);
        noticeVo.setMarkDown(str);

        // 生成html文字
        String htmlContent = "";
        // 初始化配置（支持表格、自动链接等扩展）
        MutableDataSet html_options = new MutableDataSet();
        html_options.set(Parser.EXTENSIONS,  Arrays.asList(
                TablesExtension.create(),    // 启用表格支持
                AutolinkExtension.create()   // 启用自动链接检测
        ));
        // 创建解析器和渲染器
        Parser parser = Parser.builder(html_options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(html_options).build();
        // 执行转换
        Node document = parser.parse(str);
        htmlContent = renderer.render(document);
        log.debug("生成的html文字:" + htmlContent);
        noticeVo.setHtml(htmlContent);

        // 8、插入到通知表中
        BNoticeVo entity = new BNoticeVo();
        // 0:系统通知
        entity.setType("1");
        entity.setTitle(noticeVo.getTitle());
        entity.setMsg(str);
        entity.setHtml(htmlContent);
        // 1:已发布
        entity.setStatus("1");
        /**
         * 获取要发送消息的用户
         */
        List<MStaffVo> sendStaffs = new ArrayList<>();
        for (BBpmCommentVo comment : bpmCommentVos) {
            String assignee = comment.getAssignee_code();
            MStaffVo vo = staffService.selectByCode(assignee);
            sendStaffs.add(vo);
        }

        entity.setStaff_list(sendStaffs);
        // 设置被影响的用户id
        InsertResultAo<BNoticeVo> vo = insert(entity);

        // 9、发送消息 websocket
        // 10:消息通知
        noticeVo.setType("0");
        bpmWebSocket2Utils.convertAndSendUser(sendStaffs, WebSocketConstants.WEBSOCKET_BPM_APPROVE_NOTICE, noticeVo);
    }

    /**
     * 审批拒绝通知：发送多人
     *
     * 【审批拒绝关闭提醒通知】
     * *标题*：2025年市场活动预算申请
     * *状态*：该审批已被其他处理人通过，无需您继续操作
     * *关闭原因*：或签模式下首人通过即生效
     */
    public void sendBpmRefuseNotice(ProcessInstance processInstance){
        /**
         * 获取：
         * 1、审批流名称
         * 2、申请人
         * 3、当前进度：已通过部门经理审批 → 待您审批（财务审核）
         * 4、审批内容
         */
        BpmInstanceVo bpmInstanceVo = mapper.getInstanceAndSummary(processInstance.getProcessInstanceId(), processInstance.getProcessDefinitionId());
        BBpmNoticeVo noticeVo = new BBpmNoticeVo();

//        1、审批流名称
        noticeVo.setProcess_definition_name(bpmInstanceVo.getProcess_definition_name());
        noticeVo.setTitle("【审批任务驳回通知-" + noticeVo.getProcess_definition_name()  + "】");

        // 2、申请人 提交时间
        noticeVo.setOwner_code(bpmInstanceVo.getOwner_code());
        noticeVo.setOwner_name(bpmInstanceVo.getOwner_name());
        noticeVo.setStart_time(bpmInstanceVo.getStart_time());

////        3、获取下一个审批人
//        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
//        if (StringUtils.isNotEmpty(bpmInstanceApproveEntity.getIs_next())) {
//            // 查询下一个节点审批人
//            BBpmInstanceVo _vo =  bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApproveEntity.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
//            String staff_code = _vo.getNext_approve_code();
//            noticeVo.setNext_staff_code(staff_code);
//            noticeVo.setNext_staff_name(_vo.getNext_approve_name());
//        }


//        4、获取摘要
        noticeVo.setSummary(bpmInstanceVo.getSummary());
//        5、获取审批流程：如果为空说明还在第一个节点
        List<BBpmCommentVo> bpmCommentVos = mapper.getProcessComments(bpmInstanceVo.getProcess_code());
        noticeVo.setComment(bpmCommentVos);

        // 6、编辑跳转链接
        noticeVo.setSerial_type(bpmInstanceVo.getSerial_type());
        noticeVo.setSerial_id(bpmInstanceVo.getSerial_id());
        String domain = configService.selectByKey(SystemConstants.SCM_SYSTEM_DOMAIN).getValue();
        noticeVo.setApprovalUrl(domain+"/todo/index?process_code="+bpmInstanceVo.getProcess_code());
        noticeVo.setDeadLine(LocalDateTime.now().plusDays(1));
        noticeVo.setC_time(LocalDateTime.now());


//        7、生成markdown文字
        Document doc = MarkDownNotice.createBpmRefuseNotice(noticeVo);
        MutableDataSet options = new MutableDataSet();
        String str = Formatter.builder(options).build().render(doc);
        log.debug("生成的markdown文字:" + str);
        noticeVo.setMarkDown(str);

        // 生成html文字
        String htmlContent = "";
        // 初始化配置（支持表格、自动链接等扩展）
        MutableDataSet html_options = new MutableDataSet();
        html_options.set(Parser.EXTENSIONS,  Arrays.asList(
                TablesExtension.create(),    // 启用表格支持
                AutolinkExtension.create()   // 启用自动链接检测
        ));
        // 创建解析器和渲染器
        Parser parser = Parser.builder(html_options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(html_options).build();
        // 执行转换
        Node document = parser.parse(str);
        htmlContent = renderer.render(document);
        log.debug("生成的html文字:" + htmlContent);
        noticeVo.setHtml(htmlContent);

        // 8、插入到通知表中
        BNoticeVo entity = new BNoticeVo();
        // 0:系统通知
        entity.setType("1");
        entity.setTitle(noticeVo.getTitle());
        entity.setMsg(str);
        entity.setHtml(htmlContent);
        // 1:已发布
        entity.setStatus("1");
        /**
         * 获取要发送消息的用户
         */
        List<MStaffVo> sendStaffs = new ArrayList<>();
        for (BBpmCommentVo comment : bpmCommentVos) {
            String assignee = comment.getAssignee_code();
            MStaffVo vo = staffService.selectByCode(assignee);
            sendStaffs.add(vo);
        }

        entity.setStaff_list(sendStaffs);
        // 设置被影响的用户id
        InsertResultAo<BNoticeVo> vo = insert(entity);

        // 9、发送消息 websocket
        // 10:消息通知
        noticeVo.setType("0");
        bpmWebSocket2Utils.convertAndSendUser(sendStaffs, WebSocketConstants.WEBSOCKET_BPM_REFUSE_NOTICE, noticeVo);
    }

    /**
     * 新增
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BNoticeVo> insert(BNoticeVo param) {
        BNoticeEntity entity = new BNoticeEntity();
        BeanUtilsSupport.copyProperties(param, entity);
        noticeMapper.insert(entity);

        // 新增关联关系
        noticeStaffService.insertNoticeStaff(entity.getId(), param.getStaff_list());

        BNoticeVo bNoticeVo = selectById(entity.getId());
        return InsertResultUtil.OK(bNoticeVo);
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @Override
    public BNoticeVo selectById(Integer id) {
        BNoticeVo result = new BNoticeVo();
        BNoticeEntity bNoticeEntity = noticeMapper.selectById(id);

        // 查询员工列表
        List<MStaffVo> mStaffVos = noticeStaffService.selectStaffList(id);

        BeanUtilsSupport.copyProperties(bNoticeEntity, result);
        result.setStaff_list(mStaffVos);
        return result;
    }
}
