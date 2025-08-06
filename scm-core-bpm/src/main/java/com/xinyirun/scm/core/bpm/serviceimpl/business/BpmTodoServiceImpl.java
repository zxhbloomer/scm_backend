package com.xinyirun.scm.core.bpm.serviceimpl.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.*;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceProgressVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmTodoVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import com.xinyirun.scm.core.bpm.mapper.business.*;
import com.xinyirun.scm.core.bpm.mapper.sys.file.BpmFileInfoMapper;
import com.xinyirun.scm.core.bpm.mapper.sys.file.BpmFileMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceService;
import com.xinyirun.scm.core.bpm.service.business.IBpmTodoService;
import com.xinyirun.scm.core.bpm.utils.IdWorker;
import com.xinyirun.scm.core.bpm.utils.PageUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Service
public class BpmTodoServiceImpl extends ServiceImpl<BpmTodoMapper, BpmTodoEntity> implements IBpmTodoService {

    @Autowired
    private BpmTodoMapper mapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmProcessTemplatesMapper bpmProcessTemplatesMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private BpmInstanceProcessMapper bpmInstanceProcessMapper;

    @Autowired
    private BpmInstanceApproveMapper bpmInstanceApproveMapper;

    @Autowired
    private BpmCommentMapper bpmCommentMapper;

    @Autowired
    private BpmFileMapper fileMapper;

    @Autowired
    private BpmFileInfoMapper fileInfoMapper;

    @Autowired
    private IBpmInstanceService iBpmInstanceService;

    @Autowired
    private BpmInstanceMapper bpmInstanceMapper;


    /**
     * 查看我的待办，我的已办
     *
     * @param param
     */
    @Override
    public IPage<BBpmTodoVo> selectPageList(BBpmTodoVo param) {
        // 分页条件
        Page<BBpmTodoVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());

        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        param.setUser_code(SecurityUtil.getUserSession().getStaff_info().getCode());

        return mapper.selectPageList(pageCondition, param);
    }

    /**
     * 查看我的待办
     */
    @Override
    public List<BBpmTodoVo> getListTen(BBpmTodoVo param){
        // 设置用户code
        Long staff_id = SecurityUtil.getStaff_id();
        AppStaffUserBpmInfoVo vo = mapper.getBpmDataByStaffid(staff_id);
        if(param == null){
            param = new BBpmTodoVo();
        }
        param.setUser_code(vo.getCode());
        param.setAvatar(vo.getAvatar());
        param.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);


        return mapper.getListTen( param);
    }


//    @Override
//    public HandleDataVO instanceInfo(BBpmTodoVo param) {
//        String processInstanceId = param.getProcess_instance_id();
//        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
//                .includeProcessVariables().singleResult();
//        String processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
//
//        String ex = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId()).getMainProcess().getAttributeValue("http://flowable.org/bpmn", "DingDing");
//        HashMap hashMap = JSONObject.parseObject(ex, new TypeReference<HashMap>() {
//        });
//        String processJson = MapUtil.getStr(hashMap, "processJson");
//        String formJson = MapUtil.getStr(hashMap, "formJson");
//
//        BpmProcessTemplatesEntity processTemplates = bpmProcessTemplatesMapper.selectByTemplates(processDefinitionKey.replace(PROCESS_PREFIX, ""));
//        processTemplates.setTemplate_id(processTemplates.getTemplate_id());
//        processTemplates.setName(processTemplates.getName());
//        processTemplates.setProcess_definition_id(historicProcessInstance.getProcessDefinitionId());
//        processTemplates.setProcess(processJson);
//        processTemplates.setForm_items(formJson);
//
//        HandleDataVO handleDataVO = new HandleDataVO();
//        Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
//
//        handleDataVO.setProcessInstanceId(historicProcessInstance.getId());
//        JSONObject jsonObject = (JSONObject) processVariables.get(FORM_VAR);
//        handleDataVO.setFormData(jsonObject);
//        String process = processTemplates.getProcess();
//        ChildNode childNode = JSONObject.parseObject(process, new TypeReference<ChildNode>() {
//        });
//        SettingsInfo settingsInfo = JSONObject.parseObject(processTemplates.getSettings(), new TypeReference<SettingsInfo>() {
//        });
//        Boolean sign = settingsInfo.getSign();
//        ChildNode currentNode = null;
//        if (StringUtils.isNotBlank(param.getTask_id())) {
//            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(param.getTask_id()).singleResult();
//            currentNode = getChildNode(childNode, historicTaskInstance.getTaskDefinitionKey());
//            List<FormOperates> formPerms = currentNode.getProps().getFormPerms();
//            if (CollUtil.isNotEmpty(formPerms)) {
//                Iterator<FormOperates> iterator = formPerms.iterator();
//                while (iterator.hasNext()) {
//                    FormOperates next = iterator.next();
//                    if ("H".equals(next.getPerm())) {
////                        iterator.remove();
//                        if (jsonObject != null) {
//                            jsonObject.remove(next.getId());
//                        }
//                    }
//                }
//            }
//            handleDataVO.setCurrentNode(currentNode);
//            handleDataVO.setTaskId(param.getTask_id());
//        }
//
//        if (sign) {
//            handleDataVO.setSignFlag(true);
//        } else {
//            if (StringUtils.isNotBlank(param.getTask_id())) {
//                if (currentNode != null) {
//                    if (currentNode.getProps().getSign()) {
//                        handleDataVO.setSignFlag(true);
//                    } else {
//                        handleDataVO.setSignFlag(false);
//                    }
//                }
//            } else {
//                handleDataVO.setSignFlag(false);
//            }
//        }
//
//        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
//        Map<String, List<HistoricActivityInstance>> historicActivityInstanceMap = new HashMap<>();
//        for (HistoricActivityInstance historicActivityInstance : list) {
//            List<HistoricActivityInstance> historicActivityInstances = historicActivityInstanceMap.get(historicActivityInstance.getActivityId());
//            if (historicActivityInstances == null) {
//                historicActivityInstances = new ArrayList<>();
//                historicActivityInstances.add(historicActivityInstance);
//                historicActivityInstanceMap.put(historicActivityInstance.getActivityId(), historicActivityInstances);
//            } else {
//                historicActivityInstances.add(historicActivityInstance);
//                historicActivityInstanceMap.put(historicActivityInstance.getActivityId(), historicActivityInstances);
//            }
//        }
//
//        Process mainProcess = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId()).getMainProcess();
//        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
//
//        List<String> runningList = new ArrayList<>();
//        handleDataVO.setRunningList(runningList);
//        List<String> endList = new ArrayList<>();
//        handleDataVO.setEndList(endList);
//        List<String> noTakeList = new ArrayList<>();
//        handleDataVO.setNoTakeList(noTakeList);
//        Map<String, List<TaskDetailVO>> deatailMap = new HashMap<>();
//        List<Comment> processInstanceComments = taskService.getProcessInstanceComments(historicProcessInstance.getId());
//        List<Attachment> processInstanceAttachments = taskService.getProcessInstanceAttachments(historicProcessInstance.getId());
//        for (FlowElement flowElement : flowElements) {
//            List<TaskDetailVO> detailVOList = new ArrayList<>();
//            List<HistoricActivityInstance> historicActivityInstanceList = historicActivityInstanceMap.get(flowElement.getId());
//            if (CollUtil.isNotEmpty(historicActivityInstanceList)) {
//                for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
//                    if (historicActivityInstance.getEndTime() != null) {
//                        if ("startEvent".equalsIgnoreCase(historicActivityInstance.getActivityType()) || "endEvent".equalsIgnoreCase(historicActivityInstance.getActivityType())) {
//                            TaskDetailVO taskDetailVO = new TaskDetailVO();
//                            taskDetailVO.setActivityId(historicActivityInstance.getActivityId());
//                            taskDetailVO.setName(historicActivityInstance.getActivityName());
//                            taskDetailVO.setCreateTime(historicActivityInstance.getStartTime());
//                            taskDetailVO.setEndTime(historicActivityInstance.getEndTime());
//                            detailVOList.add(taskDetailVO);
//                            deatailMap.put(historicActivityInstance.getActivityId(), detailVOList);
//                            endList.add(historicActivityInstance.getActivityId());
//                        } else if ("userTask".equalsIgnoreCase(historicActivityInstance.getActivityType())) {
//                            List<TaskDetailVO> voList = deatailMap.get(historicActivityInstance.getActivityId());
//                            List<HistoricActivityInstance> activityInstanceList = list.stream().filter(h -> h.getActivityId().equals(historicActivityInstance.getActivityId()) && h.getEndTime() != null).collect(Collectors.toList());
//                            if (voList != null) {
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            } else {
//                                voList = new ArrayList<>();
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            }
//                            deatailMap.put(historicActivityInstance.getActivityId(), voList);
//                            endList.add(historicActivityInstance.getActivityId());
//                        } else if ("serviceTask".equalsIgnoreCase(historicActivityInstance.getActivityType())) {
//
//                        }
//                    } else {
//                        if ("userTask".equalsIgnoreCase(historicActivityInstance.getActivityType())) {
//                            List<TaskDetailVO> voList = deatailMap.get(historicActivityInstance.getActivityId());
//                            List<HistoricActivityInstance> activityInstanceList = list.stream().filter(h -> h.getActivityId().equals(historicActivityInstance.getActivityId()) && h.getEndTime() == null).collect(Collectors.toList());
//                            if (voList != null) {
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            } else {
//                                voList = new ArrayList<>();
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            }
//                            deatailMap.put(historicActivityInstance.getActivityId(), voList);
//                            if (endList.contains(historicActivityInstance.getActivityId())) {
//                                endList.remove(historicActivityInstance.getActivityId());
//                                runningList.add(historicActivityInstance.getActivityId());
//                            } else {
//                                runningList.add(historicActivityInstance.getActivityId());
//                            }
//                        } else if ("serviceTask".equalsIgnoreCase(historicActivityInstance.getActivityType())) {
//
//                        }
//                    }
//                }
//            } else {
//                noTakeList.add(flowElement.getId());
//            }
//        }
//        handleDataVO.setProcessTemplates(processTemplates);
//        handleDataVO.setDetailVOList(deatailMap);
//        return handleDataVO;
//    }

    /**
     * 同意
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agree(BBpmTodoVo param) {

        // 1.查询任务
        Task task = taskService.createTaskQuery().taskId(param.getTask_id()).singleResult();
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            throw new BusinessException("委派人不可以点击同意按钮,而应该点击 委派人完成按钮");
        }

        // 2.审批流意见保存
        if(StringUtils.isNotBlank(param.getComments())){
            taskService.addComment(task.getId(),task.getProcessInstanceId(),WflowGlobalVarDef.OPINION_COMMENT, param.getComments());
        }

        // 3.审批流任务完成
        HashMap<String, Object> var = new HashMap<>();
        var.put(WflowGlobalVarDef.APPROVE + task.getId(), DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_AGREE);
        taskService.complete(task.getId(),var);

        // 4.审批流附件,评论
        if (StringUtils.isNotBlank(param.getComments())) {
            param.setTask_id(task.getId());
            param.setNode_id(task.getTaskDefinitionKey());
            param.setType(WflowGlobalVarDef.OPINION_COMMENT);
            instBpmComment(param);
        }
    }

    /**
     * 流程审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuse(BBpmTodoVo param) {

        // 1.查询任务
        String taskId = param.getTask_id();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        // 2.审批流意见保存
        if (StringUtils.isNotBlank(param.getComments())) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), WflowGlobalVarDef.OPINION_COMMENT, param.getComments());
        }

        // 3.审批流任务完成
        HashMap<String, Object> var = new HashMap<>();
        var.put(WflowGlobalVarDef.APPROVE + task.getId(), DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE);
        taskService.complete(task.getId(), var);

        // 4.审批流附件,意见
        if (StringUtils.isNotBlank(param.getComments())) {
            param.setTask_id(task.getId());
            param.setNode_id(task.getTaskDefinitionKey());
            param.setType(WflowGlobalVarDef.OPINION_COMMENT);
            param.setProcess_instance_id(task.getProcessInstanceId());
            instBpmComment(param);
        }
    }

    /**
     * 任务评论
     *
     * @param param
     */
    @Override
    @Transactional
    public void updateComments(BBpmTodoVo param) {
        String userCode = SecurityUtil.getUserSession().getStaff_info().getCode();

        // 1.查询进行中的表单数据
        List<BpmInstanceApproveEntity> bpmInstanceApproveEntities = bpmInstanceApproveMapper.selectRunIngNodeIdByTask(param.getProcess_code());
        if (CollectionUtil.isEmpty(bpmInstanceApproveEntities)){
            throw new BusinessException("审批流不存在");
        }

        // 2.当前节点用户的任务
        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveEntities.stream().filter(k -> k.getAssignee_code().equals(userCode)).findFirst().get();
        if (ObjectUtils.isEmpty(bpmInstanceApproveEntity)){
            throw new BusinessException("无评论权限");
        }

        // 3.流程记录评论
        if (bpmInstanceApproveEntity.getStatus().equals(DictConstant.B_BPM_TODO_STATUS_ZERO)) {
            if (StringUtils.isNotBlank(param.getComments())) {
                // 保存评论
                taskService.addComment(bpmInstanceApproveEntity.getTask_id(), bpmInstanceApproveEntity.getProcess_instance_id(), WflowGlobalVarDef.COMMENTS_COMMENT, param.getComments());
            }

            if (CollectionUtil.isNotEmpty(param.getAnnex_files())) {
                // 保存附件
                for (SFileInfoVo annexFile : param.getAnnex_files()) {
                    taskService.createAttachment(WflowGlobalVarDef.OPTION_COMMENT,bpmInstanceApproveEntity.getTask_id(),bpmInstanceApproveEntity.getProcess_instance_id(),annexFile.getFileName(),annexFile.getFileName(),annexFile.getUrl());
                }
            }
        }else {
            // 查询进行中的表单是由有评论节点
            BpmInstanceApproveEntity bpmInstanceApproveComment = bpmInstanceApproveMapper.selectNodeIdByNewComment(param.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
            if (ObjectUtils.isNotEmpty(bpmInstanceApproveComment)){
                // 任务已办，增加新的节点，任务 更新其上下节点
                bpmInstanceApproveEntity = updateNode(Arrays.asList(bpmInstanceApproveComment),bpmInstanceApproveComment);
            }else {
                // 任务已办，增加新的节点，任务 更新其上下节点
                bpmInstanceApproveEntity = updateNode(bpmInstanceApproveEntities,bpmInstanceApproveEntity);
            }
        }

        // 4.保存评论
        if (StringUtils.isNotBlank(param.getComments())) {
            param.setTask_id(bpmInstanceApproveEntity.getTask_id());
            param.setNode_id(bpmInstanceApproveEntity.getNode_id());
            param.setType(WflowGlobalVarDef.COMMENTS_COMMENT);
            param.setProcess_instance_id(bpmInstanceApproveEntity.getProcess_instance_id());
            instBpmComment(param);
        }
    }

    /**
     * 增加评论
     */
    private void instBpmComment(BBpmTodoVo param) {
        BpmCommentEntity bpmCommentEntity = new BpmCommentEntity();
        bpmCommentEntity.setProcess_code(param.getProcess_code());
        bpmCommentEntity.setTask_id(param.getTask_id());
        bpmCommentEntity.setNode_id(param.getNode_id());
        bpmCommentEntity.setText(param.getComments());
        bpmCommentEntity.setAssignee_code(SecurityUtil.getUserSession().getStaff_info().getCode());
        bpmCommentEntity.setAssignee_name(SecurityUtil.getUserSession().getStaff_info().getName());
        bpmCommentEntity.setType(param.getType());

        // 附件上传
        if (CollectionUtil.isNotEmpty(param.getAnnex_files())) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bpmCommentEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_B_BPM_COMMENT);
            fileMapper.insert(fileEntity);
            // 保存审批流程附件
            for (SFileInfoVo annexFile : param.getAnnex_files()) {

                // 详情表新增
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                annexFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(annexFile, fileInfoEntity);
                fileInfoEntity.setFile_name(annexFile.getFileName());
                fileInfoMapper.insert(fileInfoEntity);
            }
            bpmCommentEntity.setFiles_id(fileEntity.getId());
        }
        bpmCommentMapper.insert(bpmCommentEntity);
    }

    @Resource
    private IdWorker idWorker;

    /**
     * 任务已办，增加新的节点，任务 更新其上下节点
     */
    private BpmInstanceApproveEntity updateNode(List<BpmInstanceApproveEntity> bpmInstanceApproveEntities, BpmInstanceApproveEntity bpmInstanceApproveEntity) {

        String nodeId = "node_"+idWorker.nextId();
        // 保存审批流节点
        BpmInstanceProcessEntity bpmInstanceProcessEntity = new BpmInstanceProcessEntity();
        bpmInstanceProcessEntity.setNode_id(nodeId);
        bpmInstanceProcessEntity.setProcess_code(bpmInstanceApproveEntity.getProcess_code());
        bpmInstanceProcessEntity.setIs_next(bpmInstanceApproveEntity.getIs_next());
        bpmInstanceProcessEntity.setNode_type("COMMENT");
        bpmInstanceProcessEntity.setName("参与评论");
        bpmInstanceProcessEntity.setOwner_code(SecurityUtil.getUserSession().getStaff_info().getCode());
        bpmInstanceProcessEntity.setOwner_name(SecurityUtil.getUserSession().getStaff_info().getName());
        bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_COMPLETE);
        bpmInstanceProcessEntity.setStart_time(LocalDateTime.now());
        bpmInstanceProcessEntity.setFinish_time(LocalDateTime.now());
        bpmInstanceProcessMapper.insert(bpmInstanceProcessEntity);

        // 保存参与评论任务
        BpmInstanceApproveEntity bpmInstanceApprove = new BpmInstanceApproveEntity();
        bpmInstanceApprove.setProcess_code(bpmInstanceApproveEntity.getProcess_code());
        bpmInstanceApprove.setTask_id(nodeId);
        bpmInstanceApprove.setNode_id(nodeId);
        bpmInstanceApprove.setType(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_THREE);
        bpmInstanceApprove.setProcess_instance_id(bpmInstanceApproveEntity.getProcess_instance_id());
        bpmInstanceApprove.setProcess_definition_id(bpmInstanceApproveEntity.getProcess_definition_id());
        bpmInstanceApprove.setTask_name("参与评论");
        bpmInstanceApprove.setAssignee_code(SecurityUtil.getUserSession().getStaff_info().getCode());
        bpmInstanceApprove.setAssignee_name(SecurityUtil.getUserSession().getStaff_info().getName());
        bpmInstanceApprove.setForm_items(bpmInstanceApproveEntity.getForm_items());
        bpmInstanceApprove.setProcess(bpmInstanceApproveEntity.getProcess());
        bpmInstanceApprove.setSerial_id(bpmInstanceApproveEntity.getSerial_id());
        bpmInstanceApprove.setSerial_type(bpmInstanceApproveEntity.getSerial_type());
        bpmInstanceApprove.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_TWO);
        bpmInstanceApprove.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ONE);
        bpmInstanceApprove.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_COMMENT);
        bpmInstanceApprove.setIs_next(bpmInstanceApproveEntity.getIs_next());
        bpmInstanceApprove.setRemark(null);
        bpmInstanceApprove.setDue_date(null);
        bpmInstanceApprove.setApprove_time(LocalDateTime.now());
        bpmInstanceApproveMapper.insert(bpmInstanceApprove);

        // 更新任务的下一个节点
        for (BpmInstanceApproveEntity instanceApproveEntity : bpmInstanceApproveEntities) {
            instanceApproveEntity.setIs_next(nodeId);
            bpmInstanceApproveMapper.updateById(instanceApproveEntity);
        }

        // 更新当前节点
        BpmInstanceProcessEntity instanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstance(bpmInstanceApproveEntity.getNode_id(), bpmInstanceApproveEntity.getProcess_code());
        instanceProcessEntity.setIs_next(nodeId);
        bpmInstanceProcessMapper.updateById(instanceProcessEntity);

        return bpmInstanceApprove;
    }

    /**
     * 通过流程实例id查看详情
     */
    @Override
    public BBpmInstanceProgressVo getInstanceProgress(BBpmInstanceProgressVo param) {
        BBpmInstanceProgressVo bBpmTodoProgressVo =  mapper.getInstanceProgress(param);

        // 1.判断当前用户是否任务创建人
        String userCode = SecurityUtil.getUserSession().getStaff_info().getCode();
        bBpmTodoProgressVo.setIf_owner_user(bBpmTodoProgressVo.getOwner_user().getCode().equals(userCode));

        // 2.判断当前用户是否是节点任务的拥有者
        BpmTodoEntity bpmTodoEntity = mapper.selectRunIngNodeIdByTask(bBpmTodoProgressVo.getProcess_code(),userCode);
        if (bpmTodoEntity!=null) {
            bBpmTodoProgressVo.setCurrent_task_id(bpmTodoEntity.getTask_id());
            bBpmTodoProgressVo.setIf_approve_user(true);
        } else {
            bBpmTodoProgressVo.setIf_approve_user(false);
        }

        return bBpmTodoProgressVo;
    }

    /**
     * 通过流程实例id查看详情
     */
    @Override
    public BBpmInstanceProgressVo getInstanceProgressapp(BBpmInstanceProgressVo param) {
        BBpmInstanceProgressVo bBpmTodoProgressVo =  mapper.getInstanceProgress(param);
        AppStaffUserBpmInfoVo vo = mapper.getBpmDataByStaffid(SecurityUtil.getStaff_id());

        // 1.判断当前用户是否任务创建人
        String userCode = vo.getCode();
        bBpmTodoProgressVo.setIf_owner_user(bBpmTodoProgressVo.getOwner_user().getCode().equals(userCode));

        // 2.判断当前用户是否是节点任务的拥有者
        BpmTodoEntity bpmTodoEntity = mapper.selectRunIngNodeIdByTask(bBpmTodoProgressVo.getProcess_code(),userCode);
        if (bpmTodoEntity!=null) {
            bBpmTodoProgressVo.setCurrent_task_id(bpmTodoEntity.getTask_id());
            bBpmTodoProgressVo.setIf_approve_user(true);
        } else {
            bBpmTodoProgressVo.setIf_approve_user(false);
        }

        return bBpmTodoProgressVo;
    }

    /**
     * 流程撤销
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(BBpmTodoVo param) throws Exception {

        // 1.查询任务
        HistoricTaskInstance task = null;
        if(null == param.getTask_id()){
            // 获取流程实例ID
            String processInstanceId = param.getProcess_instance_id();
            
            // 如果流程实例ID为空，通过流程编号查询bpm_instance表获取
            if(StringUtils.isBlank(processInstanceId)){
                if(StringUtils.isBlank(param.getProcess_code())){
                    throw new BusinessException("task_id、process_instance_id、process_code至少需要提供一个参数");
                }
                
                // 通过process_code查询bpm_instance获取process_instance_id
                processInstanceId = bpmInstanceMapper.selectProcessInstanceIdByCode(param.getProcess_code());
                
                if(StringUtils.isBlank(processInstanceId)){
                    throw new BusinessException("根据流程编号未找到对应的流程实例");
                }
            }
            
            //通过流程实例id找最新的taskId
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId).orderByTaskId().desc().list();
            if(CollUtil.isNotEmpty(list)){
                task = list.get(0);
            }
        }else {
            task = historyService.createHistoricTaskInstanceQuery().taskId(param.getTask_id()).singleResult();
        }
        if (task == null) {
            throw new BusinessException("找不到任务");
        }

        // 2.保存审批流评论
        if(StringUtils.isNotBlank(param.getComments())){
            taskService.addComment(task.getId(),task.getProcessInstanceId(),WflowGlobalVarDef.OPINION_COMMENT,param.getComments());
        }

        // 3.流程撤销
        List<Execution> executions = runtimeService.createExecutionQuery()
                .parentId(task.getProcessInstanceId())
                .onlyChildExecutions().list();
        // 强制流程指向驳回
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), "cancel-end")
                .moveExecutionsToSingleActivityId(executions.stream().map(Execution::getId)
                        .collect(Collectors.toList()), "cancel-end")
                .changeState();

        // 4.保存流程评论，附件
        if (StringUtils.isNotBlank(param.getComments())) {
            param.setTask_id(task.getId());
            param.setNode_id(task.getTaskDefinitionKey());
            param.setType(WflowGlobalVarDef.OPINION_COMMENT);
            param.setComments("撤销:" + param.getComments());
            param.setProcess_instance_id(task.getProcessInstanceId());
            instBpmComment(param);
        }

        // 5.更新相关节点信息
        iBpmInstanceService.saveProcessInstanceCancelled(task.getId());
    }


    /**
     * 任务审批转办
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(BBpmTodoVo param) {

        // 1.查询任务
        String taskId = param.getTask_id();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        // 2.保存变量，任务最新受让人
        Map<String, Object> map = new HashMap<>();
        map.put(WflowGlobalVarDef.TRANSFER_ASSIGNEE_CODE+taskId, param.getAssignee_code());
        runtimeService.setVariables(task.getProcessInstanceId(), map);

        // 3.审批流意见保存
        if (StringUtils.isNotBlank(param.getComments())) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), WflowGlobalVarDef.OPINION_COMMENT, param.getComments());
        }

        // 5,任务转交
        taskService.setOwner(task.getId(), SecurityUtil.getUserSession().getStaff_info().getCode());
        taskService.setAssignee(task.getId(), param.getAssignee_code());

        // 6.审批流附件,意见
        if (StringUtils.isNotBlank(param.getComments())) {
        param.setTask_id(task.getId());
        param.setNode_id(task.getTaskDefinitionKey());
        param.setType(WflowGlobalVarDef.OPINION_COMMENT);
        param.setProcess_instance_id(task.getProcessInstanceId());
        instBpmComment(param);
        }
    }

    /**
     * todo 任务审批后加签
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterAdd(BBpmTodoVo param) {

        // 1.查询任务
        String taskId = param.getTask_id();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        // 2.审批流意见保存
        if (StringUtils.isNotBlank(param.getComments())) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), WflowGlobalVarDef.OPINION_COMMENT, param.getComments());
        }

        // 4.审批流附件保存
    }

    /**
     * 任务审批后加签
     */
    public Integer selectTodoCount(BBpmTodoVo param) {
        if(param == null){
            param = new BBpmTodoVo();
        }
        param.setUser_code(SecurityUtil.getUserSession().getStaff_info().getCode());
        param.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
        return mapper.selectTodoCount(param);
    }
}
