//package com.xinyirun.scm.core.bpm.serviceimpl.business;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.map.MapUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.alibaba.fastjson2.TypeReference;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.xinyirun.scm.bean.bpm.dto.HandleDataDTO;
//import com.xinyirun.scm.bean.bpm.dto.json.ChildNode;
//import com.xinyirun.scm.bean.bpm.dto.json.FormOperates;
//import com.xinyirun.scm.bean.bpm.dto.json.SettingsInfo;
//import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
//import com.xinyirun.scm.bean.bpm.vo.*;
//import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
//import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
//import com.xinyirun.scm.bean.entity.busniess.allocate.BAllocateOrderEntity;
//import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
//import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmGroupVo;
//import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
//import com.xinyirun.scm.bean.utils.security.SecurityUtil;
//import com.xinyirun.scm.common.bpm.WorkFlowConstants;
//import com.xinyirun.scm.common.exception.system.BusinessException;
//import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
//import com.xinyirun.scm.core.bpm.mapper.business.BpmCcMapper;
//import com.xinyirun.scm.core.bpm.mapper.business.BpmFormGroupsMapper;
//import com.xinyirun.scm.core.bpm.mapper.business.BpmProcessTemplatesMapper;
//import com.xinyirun.scm.core.bpm.service.business.IBpmProcessWorkService;
//import com.xinyirun.scm.core.bpm.service.business.IBpmUsersService;
//import com.xinyirun.scm.core.bpm.utils.PageUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.flowable.bpmn.model.BpmnModel;
//import org.flowable.bpmn.model.FlowElement;
//import org.flowable.bpmn.model.Process;
//import org.flowable.common.engine.impl.identity.Authentication;
//import org.flowable.engine.HistoryService;
//import org.flowable.engine.RepositoryService;
//import org.flowable.engine.RuntimeService;
//import org.flowable.engine.TaskService;
//import org.flowable.engine.history.HistoricActivityInstance;
//import org.flowable.engine.history.HistoricProcessInstance;
//import org.flowable.engine.repository.ProcessDefinition;
//import org.flowable.engine.runtime.ActivityInstance;
//import org.flowable.engine.runtime.ProcessInstance;
//import org.flowable.engine.task.Attachment;
//import org.flowable.engine.task.Comment;
//import org.flowable.task.api.DelegationState;
//import org.flowable.task.api.Task;
//import org.flowable.task.api.history.HistoricTaskInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import static com.xinyirun.scm.common.bpm.CommonConstants.*;
//import static com.xinyirun.scm.common.bpm.WorkFlowConstants.*;
//import static com.xinyirun.scm.core.bpm.utils.BpmnModelUtils.getChildNode;
//
///**
// * <p>
// * process_templates 服务实现类
// * </p>
// *
// * @author xinyirun
// * @since 2024-10-11
// */
//@Service
//public class BpmProcessWorkServiceImpl extends ServiceImpl<BpmProcessTemplatesMapper, BpmProcessTemplatesEntity> implements IBpmProcessWorkService {
//
//
//    @Autowired
//    private BpmProcessTemplatesMapper mapper;
//
//    @Autowired
//    private RepositoryService repositoryService;
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private RuntimeService runtimeService;
//
//    @Autowired
//    private BpmFormGroupsMapper bpmFormGroupsMapper;
//
//    @Autowired
//    private IBpmUsersService bpmUsersService;
//
//    @Autowired
//    private HistoryService historyService;
//
//    @Autowired
//    private BpmCcMapper bpmCcMapper;
//
//
//    @Override
//    public IPage<BBpmProcessVo> selectPage(BBpmProcessVo searchCondition) {
//        // 分页条件
//        Page<BAllocateOrderEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
//        // 通过page进行排序
//        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//
//        return mapper.selectPage(pageCondition, searchCondition);
//    }
//
//    /**
//     * 获取详情
//     */
//    @Override
//    public BBpmProcessVo selectById(Integer id) {
//        BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
//        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = mapper.selectById(id);
//        BeanUtilsSupport.copyProperties(bpmProcessTemplatesEntity, bBpmProcessVo);
//
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(WorkFlowConstants.PROCESS_PREFIX + bpmProcessTemplatesEntity.getTemplate_id()).latestVersion().singleResult();
////        if(processDefinition == null){
////            throw new BusinessException("该流程暂未接入Flowable,请重试");
////        }
//        bBpmProcessVo.setProcess_definition_id(processDefinition == null ? null : processDefinition.getId());
//        return bBpmProcessVo;
//    }
//
//    /**
//     * 获取模板分组
//     */
//    @Override
//    public List<BBpmGroupVo> getGroup() {
//        return bpmFormGroupsMapper.getGroup();
//    }
//
//    /**
//     * 查看我的代办
//     *
//     * @param param
//     */
//    @Override
//    public IPage<TaskVO> toDoList(BBpmProcessVo param) {
//        List<Task> tasks = taskService.createTaskQuery().taskAssignee(SecurityUtil.getUserSession().getStaff_info().getCode())
//                .includeProcessVariables()
//                .orderByTaskCreateTime().desc()
//                .listPage((int) (Integer.valueOf((int) (param.getPageCondition().getCurrent() - 1)) * param.getPageCondition().getSize()), (int) param.getPageCondition().getSize());
//        long count = taskService.createTaskQuery().taskAssignee(SecurityUtil.getUserSession().getStaff_info().getCode()).count();
//        List<TaskVO> taskVOS= new ArrayList<>();
//
//        List<String> userCodes= new ArrayList<>();
//        for (Task task : tasks) {
//            Map<String, Object> processVariables = task.getProcessVariables();
//            String code = JSONObject.parseObject(MapUtil.getStr(processVariables, START_USER_INFO), new TypeReference<UserInfo>() {
//            }).getCode();
//            userCodes.add(code);
//        }
//
//
//        Map<String, BpmUsersEntity> collect= new HashMap<>();
//        if(CollUtil.isNotEmpty(userCodes)){
//            LambdaQueryWrapper<BpmUsersEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.in(BpmUsersEntity::getUser_code,userCodes);
//            List<BpmUsersEntity> list = bpmUsersService.list(lambdaQueryWrapper);
//            collect = list.stream().collect(Collectors.toMap(BpmUsersEntity::getUser_code, Function.identity()));
//        }
//
//        for (Task task : tasks) {
//            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
//            Map<String, Object> processVariables = task.getProcessVariables();
//            TaskVO taskVO = new TaskVO();
//            taskVO.setTaskId(task.getId());
//            taskVO.setProcessInstanceId(task.getProcessInstanceId());
//            taskVO.setProcessDefinitionName(bpmnModel.getMainProcess().getName());
//            taskVO.setStartUser(JSONObject.parseObject(MapUtil.getStr(processVariables,START_USER_INFO),new TypeReference<UserInfo>(){}));
//            taskVO.setUsers(collect.get(SecurityUtil.getUserSession().getStaff_info().getCode()));
//            taskVO.setStartTime(processInstance.getStartTime());
//            taskVO.setCurrentActivityName(getCurrentName(processInstance.getId(),false,processInstance.getProcessDefinitionId()));
//
//            taskVO.setBusinessStatus(MapUtil.getStr(processVariables,PROCESS_STATUS));
//            taskVO.setTaskCreatedTime(task.getCreateTime());
//            DelegationState delegationState = task.getDelegationState();
//            if(delegationState!=null){
//                taskVO.setDelegationState(delegationState);
//            }
//            taskVOS.add(taskVO);
//        }
//
//        IPage<TaskVO> bBpmProcessVoIPage = new Page<>();
//
//        bBpmProcessVoIPage.setRecords(taskVOS);
//        bBpmProcessVoIPage.setCurrent(param.getPageCondition().getCurrent());
//        bBpmProcessVoIPage.setSize(param.getPageCondition().getSize());
//        bBpmProcessVoIPage.setTotal(count);
//        return bBpmProcessVoIPage;
//    }
//
//
//    /**
//     * 查看我发起的流程
//     *
//     * @param param
//     */
//    @Override
//    public IPage<HistoryProcessInstanceVO> applyList(BBpmProcessVo param) {
//        List<HistoricProcessInstance> historicProcessInstances =
//                historyService.createHistoricProcessInstanceQuery()
//                        .includeProcessVariables()
//                        .startedBy(SecurityUtil.getUserSession().getStaff_info().getCode())
//                        .orderByProcessInstanceStartTime().desc()
//                        .listPage((int)param.getPageCondition().getCurrent() - 1 * (int)param.getPageCondition().getSize(), (int)param.getPageCondition().getSize());
//
//        long count = historyService.createHistoricProcessInstanceQuery().startedBy(SecurityUtil.getUserSession().getStaff_info().getCode()).count();
//        List<String> applyUserCode = new ArrayList<>();
//        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
//            Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
//            String code = JSONObject.parseObject(MapUtil.getStr(processVariables, START_USER_INFO), new TypeReference<UserInfo>() {}).getCode();
//
//            applyUserCode.add(code);
//        }
//        Map<String, BpmUsersEntity> collect= new HashMap<>();
//        if(CollUtil.isNotEmpty(applyUserCode)){
//            LambdaQueryWrapper<BpmUsersEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.in(BpmUsersEntity::getUser_code,applyUserCode);
//            List<BpmUsersEntity> list = bpmUsersService.list(lambdaQueryWrapper);
//            collect = list.stream().collect(Collectors.toMap(BpmUsersEntity::getUser_code, Function.identity()));
//        }
//
//        List<HistoryProcessInstanceVO> historyProcessInstanceVOS= new ArrayList<>();
//
//        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
//            Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
//            HistoryProcessInstanceVO historyProcessInstanceVO=new HistoryProcessInstanceVO();
//            historyProcessInstanceVO.setProcessInstanceId(historicProcessInstance.getId());
//            historyProcessInstanceVO.setProcessDefinitionName(historicProcessInstance.getProcessDefinitionName());
//            historyProcessInstanceVO.setStartUser(JSONObject.parseObject(MapUtil.getStr(processVariables,START_USER_INFO),new TypeReference<UserInfo>(){}));
//            historyProcessInstanceVO.setUsers(collect.get(historyProcessInstanceVO.getStartUser().getCode()));
//            historyProcessInstanceVO.setStartTime(historicProcessInstance.getStartTime());
//            historyProcessInstanceVO.setEndTime(historicProcessInstance.getEndTime());
//            Boolean flag= historicProcessInstance.getEndTime() != null;
//            historyProcessInstanceVO.setCurrentActivityName(getCurrentName(historicProcessInstance.getId(),flag,historicProcessInstance.getProcessDefinitionId()));
//            historyProcessInstanceVO.setBusinessStatus(MapUtil.getStr(processVariables,PROCESS_STATUS));
//
//
//            long totalTimes = historicProcessInstance.getEndTime()==null?
//                    (Calendar.getInstance().getTimeInMillis()-historicProcessInstance.getStartTime().getTime()):
//                    (historicProcessInstance.getEndTime().getTime()-historicProcessInstance.getStartTime().getTime());
//            long dayCount = totalTimes /(1000*60*60*24);//计算天
//            long restTimes = totalTimes %(1000*60*60*24);//剩下的时间用于计于小时
//            long hourCount = restTimes/(1000*60*60);//小时
//            restTimes = restTimes % (1000*60*60);
//            long minuteCount = restTimes / (1000*60);
//
//            String spendTimes = dayCount+"天"+hourCount+"小时"+minuteCount+"分";
//            historyProcessInstanceVO.setDuration(spendTimes);
//            historyProcessInstanceVOS.add(historyProcessInstanceVO);
//        }
//
//        IPage<HistoryProcessInstanceVO> bBpmProcessVoIPage = new Page<>();
//        bBpmProcessVoIPage.setRecords(historyProcessInstanceVOS);
//        bBpmProcessVoIPage.setCurrent(param.getPageCondition().getCurrent());
//        bBpmProcessVoIPage.setSize(param.getPageCondition().getSize());
//        bBpmProcessVoIPage.setTotal(count);
//        return bBpmProcessVoIPage;
//    }
//
//    /**
//     * 同意
//     *
//     * @param handleDataDTO
//     */
//    @Override
//    public void agree(HandleDataDTO handleDataDTO) {
//        //UserInfo currentUserInfo = handleDataDTO.getCurrentUserInfo();
//
//        //附件保存
//        //List<AttachmentDTO> attachments = handleDataDTO.getAttachments();
//
//        // 意见保存
//        //String comments = handleDataDTO.getComments();
//
//        JSONObject formData = handleDataDTO.getFormData();
//        String taskId = handleDataDTO.getTaskId();
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        if (DelegationState.PENDING.equals(task.getDelegationState())) {
//            throw new BusinessException("委派人不可以点击同意按钮,而应该点击 委派人完成按钮");
//        }
//        Map<String, Object> map = new HashMap<>();
//        if (formData != null && formData.size() > 0) {
//            Map formValue = JSONObject.parseObject(formData.toJSONString(), new TypeReference<Map>() {
//            });
//            map.putAll(formValue);
//            map.put(FORM_VAR, formData);
//        }
//
//        runtimeService.setVariables(task.getProcessInstanceId(),map);
//        Authentication.setAuthenticatedUserId(SecurityUtil.getUserSession().getStaff_info().getCode());
//
//        // 意见保存
//        /*if(StringUtils.isNotBlank(comments)){
//            taskService.addComment(task.getId(),task.getProcessInstanceId(),OPINION_COMMENT,comments);
//        }*/
//
//        // 附件保存
//        /*if(attachments!=null && attachments.size()>0){
//            for (AttachmentDTO attachment : attachments) {
//                taskService.createAttachment(OPTION_COMMENT,taskId,task.getProcessInstanceId(),attachment.getName(),attachment.getName(),attachment.getUrl());
//            }
//        }*/
//
//        // 手动签名画板
//       /* if(StringUtils.isNotBlank(handleDataDTO.getSignInfo())){
//            taskService.addComment(task.getId(),task.getProcessInstanceId(),SIGN_COMMENT,handleDataDTO.getSignInfo());
//        }*/
//
//
//        taskService.complete(task.getId());
//    }
//
//    /**
//     * 通过id实例查看详情
//     *
//     * @param handleDataDTO
//     */
//    @Override
//    public HandleDataVO instanceInfo(HandleDataDTO handleDataDTO) {
//        String processInstanceId = handleDataDTO.getProcessInstanceId();
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
//        BpmProcessTemplatesEntity processTemplates = mapper.selectByTemplates(processDefinitionKey.replace(PROCESS_PREFIX,""));
//        processTemplates.setTemplate_id(processTemplates.getTemplate_id());
//        processTemplates.setName(processTemplates.getName());
//        processTemplates.setProcess_definition_id(historicProcessInstance.getProcessDefinitionId());
//        processTemplates.setProcess(processJson);
//        processTemplates.setForm_items(formJson);
//
//        HandleDataVO handleDataVO =new HandleDataVO();
//        Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
//
//        handleDataVO.setProcessInstanceId(historicProcessInstance.getId());
//        JSONObject jsonObject = (JSONObject) processVariables.get(FORM_VAR);
//        handleDataVO.setFormData(jsonObject);
//        String process = processTemplates.getProcess();
//        ChildNode childNode = JSONObject.parseObject(process, new TypeReference<ChildNode>(){});
//        SettingsInfo settingsInfo = JSONObject.parseObject(processTemplates.getSettings(), new TypeReference<SettingsInfo>() {});
//        Boolean sign = settingsInfo.getSign();
//        ChildNode currentNode=null;
//        if(StringUtils.isNotBlank(handleDataDTO.getTaskId())){
//            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(handleDataDTO.getTaskId()).singleResult();
//            currentNode = getChildNode(childNode, historicTaskInstance.getTaskDefinitionKey());
//            List<FormOperates> formPerms = currentNode.getProps().getFormPerms();
//            if(CollUtil.isNotEmpty(formPerms)){
//                Iterator<FormOperates> iterator = formPerms.iterator();
//                while (iterator.hasNext()){
//                    FormOperates next = iterator.next();
//                    if("H".equals(next.getPerm())){
////                        iterator.remove();
//                        if(jsonObject!=null){
//                            jsonObject.remove(next.getId());
//                        }
//                    }
//                }
//            }
//            handleDataVO.setCurrentNode(currentNode);
//            handleDataVO.setTaskId(handleDataDTO.getTaskId());
//        }
//
//        if(sign){
//            handleDataVO.setSignFlag(true);
//        }
//        else{
//            if(StringUtils.isNotBlank(handleDataDTO.getTaskId())){
//                if(currentNode!=null){
//                    if(currentNode.getProps().getSign()){
//                        handleDataVO.setSignFlag(true);
//                    }
//                    else{
//                        handleDataVO.setSignFlag(false);
//                    }
//                }
//            } else {
//                handleDataVO.setSignFlag(false);}
//        }
//
//        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
//        Map<String,List<HistoricActivityInstance>> historicActivityInstanceMap =new HashMap<>();
//        for (HistoricActivityInstance historicActivityInstance : list) {
//            List<HistoricActivityInstance> historicActivityInstances = historicActivityInstanceMap.get(historicActivityInstance.getActivityId());
//            if(historicActivityInstances==null){
//                historicActivityInstances =new ArrayList<>();
//                historicActivityInstances.add(historicActivityInstance);
//                historicActivityInstanceMap.put(historicActivityInstance.getActivityId(),historicActivityInstances);
//            }
//            else{
//                historicActivityInstances.add(historicActivityInstance);
//                historicActivityInstanceMap.put(historicActivityInstance.getActivityId(),historicActivityInstances);
//            }
//        }
//
//        Process mainProcess = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId()).getMainProcess();
//        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
//
//        List<String> runningList= new ArrayList<>();
//        handleDataVO.setRunningList(runningList);
//        List<String> endList=new ArrayList<>();
//        handleDataVO.setEndList(endList);
//        List<String> noTakeList=new ArrayList<>();
//        handleDataVO.setNoTakeList(noTakeList);
//        Map<String,List<TaskDetailVO>> deatailMap =new HashMap<>();
//        List<Comment> processInstanceComments = taskService.getProcessInstanceComments(historicProcessInstance.getId());
//        List<Attachment> processInstanceAttachments = taskService.getProcessInstanceAttachments(historicProcessInstance.getId());
//        for (FlowElement flowElement : flowElements) {
//            List<TaskDetailVO> detailVOList =new ArrayList<>();
//            List<HistoricActivityInstance> historicActivityInstanceList = historicActivityInstanceMap.get(flowElement.getId());
//            if(CollUtil.isNotEmpty(historicActivityInstanceList)){
//                for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
//                    if(historicActivityInstance.getEndTime()!=null){
//                        if("startEvent".equalsIgnoreCase(historicActivityInstance.getActivityType()) ||"endEvent".equalsIgnoreCase(historicActivityInstance.getActivityType())){
//                            TaskDetailVO taskDetailVO = new TaskDetailVO();
//                            taskDetailVO.setActivityId(historicActivityInstance.getActivityId());
//                            taskDetailVO.setName(historicActivityInstance.getActivityName());
//                            taskDetailVO.setCreateTime(historicActivityInstance.getStartTime());
//                            taskDetailVO.setEndTime(historicActivityInstance.getEndTime());
//                            detailVOList.add(taskDetailVO);
//                            deatailMap.put(historicActivityInstance.getActivityId(),detailVOList);
//                            endList.add(historicActivityInstance.getActivityId());
//                        }
//                        else if ("userTask".equalsIgnoreCase(historicActivityInstance.getActivityType())){
//                            List<TaskDetailVO> voList = deatailMap.get(historicActivityInstance.getActivityId());
//                            List<HistoricActivityInstance> activityInstanceList = list.stream().filter(h -> h.getActivityId().equals(historicActivityInstance.getActivityId()) &&h.getEndTime()!=null).collect(Collectors.toList());
//                            if(voList!=null){
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            }
//                            else{
//                                voList=new ArrayList<>();
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            }
//                            deatailMap.put(historicActivityInstance.getActivityId(),voList);
//                            endList.add(historicActivityInstance.getActivityId());
//                        }
//                        else if("serviceTask".equalsIgnoreCase(historicActivityInstance.getActivityType())){
//
//                        }
//                    }
//                    else{
//                        if ("userTask".equalsIgnoreCase(historicActivityInstance.getActivityType())){
//                            List<TaskDetailVO> voList = deatailMap.get(historicActivityInstance.getActivityId());
//                            List<HistoricActivityInstance> activityInstanceList = list.stream().filter(h -> h.getActivityId().equals(historicActivityInstance.getActivityId()) &&h.getEndTime()==null).collect(Collectors.toList());
//                            if(voList!=null){
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            }
//                            else{
//                                voList=new ArrayList<>();
//                                collectUserTaskInfo(processInstanceComments, processInstanceAttachments, historicActivityInstance, voList, activityInstanceList);
//                            }
//                            deatailMap.put(historicActivityInstance.getActivityId(),voList);
//                            if(endList.contains(historicActivityInstance.getActivityId())){
//                                endList.remove(historicActivityInstance.getActivityId());
//                                runningList.add(historicActivityInstance.getActivityId());
//                            }
//                            else{
//                                runningList.add(historicActivityInstance.getActivityId());
//                            }
//                        }
//                        else if("serviceTask".equalsIgnoreCase(historicActivityInstance.getActivityType())){
//
//                        }
//                    }
//                }
//            }
//            else{
//                noTakeList.add(flowElement.getId());
//            }
//        }
//        handleDataVO.setProcessTemplates(processTemplates);
//        handleDataVO.setDetailVOList(deatailMap);
//        return handleDataVO;
//    }
//
//
//
//    /**
//     * 查看我的已办
//     *
//     * @param param
//     */
//    @Override
//    public IPage<TaskVO> doneList(BBpmProcessVo param) {
//        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee(SecurityUtil.getUserSession().getStaff_info().getCode())
//                .finished()
//                .includeProcessVariables()
//                .orderByTaskCreateTime().desc()
//                .listPage((int)param.getPageCondition().getCurrent() - 1 * (int)param.getPageCondition().getSize(), (int)param.getPageCondition().getSize());
//        long count = historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee(SecurityUtil.getUserSession().getStaff_info().getCode()).finished().count();
//        List<TaskVO> taskVOS= new ArrayList<>();
//        Page<TaskVO> page =new Page<>();
//
//        List<String> userCode= new ArrayList<>();
//        for (HistoricTaskInstance historicTaskInstance : tasks) {
//            Map<String, Object> processVariables = historicTaskInstance.getProcessVariables();
//            String code = JSONObject.parseObject(MapUtil.getStr(processVariables, START_USER_INFO), new TypeReference<UserInfo>() {
//            }).getCode();
//
//            userCode.add(code);
//        }
//
//        Map<String, BpmUsersEntity> collect= new HashMap<>();
//        if(CollUtil.isNotEmpty(userCode)){
//            LambdaQueryWrapper<BpmUsersEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.in(BpmUsersEntity::getUser_code,userCode);
//            List<BpmUsersEntity> list = bpmUsersService.list(lambdaQueryWrapper);
//            collect = list.stream().collect(Collectors.toMap(BpmUsersEntity::getUser_code, Function.identity()));
//        }
//
//
//        for (HistoricTaskInstance task : tasks) {
//            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//            Boolean flag=historicProcessInstance.getEndTime()==null?false:true;
//            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
//            Map<String, Object> processVariables = task.getProcessVariables();
//            TaskVO taskVO=new TaskVO();
//            taskVO.setTaskId(task.getId());
//            taskVO.setTaskName(task.getName());
//            taskVO.setProcessInstanceId(task.getProcessInstanceId());
//            taskVO.setProcessDefinitionName(bpmnModel.getMainProcess().getName());
//            taskVO.setStartUser(JSONObject.parseObject(MapUtil.getStr(processVariables,START_USER_INFO),new TypeReference<UserInfo>(){}));
//            taskVO.setUsers(collect.get(Long.valueOf(taskVO.getStartUser().getId())));
//            taskVO.setStartTime(historicProcessInstance.getStartTime());
//            taskVO.setCurrentActivityName(getCurrentName(task.getProcessInstanceId(),flag,task.getProcessDefinitionId()));
//            taskVO.setBusinessStatus(MapUtil.getStr(processVariables,PROCESS_STATUS));
//            taskVO.setEndTime(task.getEndTime());
//
//            long totalTimes = task.getEndTime()==null?
//                    (Calendar.getInstance().getTimeInMillis()-task.getStartTime().getTime()):
//                    (task.getEndTime().getTime()-task.getStartTime().getTime());
//            long dayCount = totalTimes /(1000*60*60*24);//计算天
//            long restTimes = totalTimes %(1000*60*60*24);//剩下的时间用于计于小时
//            long hourCount = restTimes/(1000*60*60);//小时
//            restTimes = restTimes % (1000*60*60);
//            long minuteCount = restTimes / (1000*60);
//            String spendTimes = dayCount+"天"+hourCount+"小时"+minuteCount+"分";
//            taskVO.setDuration(spendTimes);
//            taskVOS.add(taskVO);
//        }
//
//        IPage<TaskVO> bBpmProcessVoIPage = new Page<>();
//
//        bBpmProcessVoIPage.setRecords(taskVOS);
//        bBpmProcessVoIPage.setCurrent(param.getPageCondition().getCurrent());
//        bBpmProcessVoIPage.setSize(param.getPageCondition().getSize());
//        bBpmProcessVoIPage.setTotal(count);
//        return bBpmProcessVoIPage;
//    }
//
//    /**
//     * 查看抄送我的
//     *
//     * @param param
//     */
//    @Override
//    public IPage<TaskVO> ccList(BBpmProcessVo param) {
//
//        // 分页条件
//        Page<BInEntity> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
//
////        // 通过page进行排序
////        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
//
//        param.setUser_code(SecurityUtil.getUserSession().getStaff_info().getCode());
//
//        Page page1 = bpmCcMapper.selectPagesOne(pageCondition,param);
//
//        List<BpmCcVo> ccList = page1.getRecords();
//        if(CollUtil.isNotEmpty(ccList)){
//            Set<String> processInstanceIds= new HashSet<>();
//            for (BpmCcVo cc : ccList) {
//                processInstanceIds.add(cc.getProcess_instance_id());
//            }
//            List<HistoricProcessInstance> processInstanceList = historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).includeProcessVariables().list();
//            Map<String,HistoricProcessInstance> map =new HashMap<>();
//            for (HistoricProcessInstance historicProcessInstance : processInstanceList) {
//                map.put(historicProcessInstance.getId(),historicProcessInstance);
//            }
//
//            List<String> userCodes= new ArrayList<>();
//            for (HistoricProcessInstance historicProcessInstance : processInstanceList) {
//                Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
//                String code = JSONObject.parseObject(MapUtil.getStr(processVariables, START_USER_INFO), new TypeReference<UserInfo>() {
//                }).getCode();
//                userCodes.add(code);
//            }
//            Map<String, BpmUsersEntity> collect= new HashMap<>();
//            if(CollUtil.isNotEmpty(userCodes)){
//                LambdaQueryWrapper<BpmUsersEntity> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
//                userLambdaQueryWrapper.in(BpmUsersEntity::getUser_code,userCodes);
//                List<BpmUsersEntity> list = bpmUsersService.list(userLambdaQueryWrapper);
//                collect = list.stream().collect(Collectors.toMap(BpmUsersEntity::getUser_code, Function.identity()));
//            }
//
//
//            for (BpmCcVo cc : ccList) {
//                HistoricProcessInstance historicProcessInstance = map.get(cc.getProcess_instance_id());
//                Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
//                cc.setProcess_instance_id(historicProcessInstance.getId());
//                cc.setProcessDefinitionName(historicProcessInstance.getProcessDefinitionName());
//                cc.setStartUser(JSONObject.parseObject(MapUtil.getStr(processVariables,START_USER_INFO),new TypeReference<UserInfo>(){}));
//                cc.setUsers(collect.get(cc.getStartUser().getCode()));
//                cc.setStartTime(historicProcessInstance.getStartTime());
//                cc.setEndTime(historicProcessInstance.getEndTime());
//                Boolean flag= historicProcessInstance.getEndTime() != null;
//                cc.setCurrentActivityName(getCurrentName(historicProcessInstance.getId(),flag,historicProcessInstance.getProcessDefinitionId()));
//                cc.setBusinessStatus(MapUtil.getStr(processVariables,PROCESS_STATUS));
//
//
//                long totalTimes = historicProcessInstance.getEndTime()==null?
//                        (Calendar.getInstance().getTimeInMillis()-historicProcessInstance.getStartTime().getTime()):
//                        (historicProcessInstance.getEndTime().getTime()-historicProcessInstance.getStartTime().getTime());
//                long dayCount = totalTimes /(1000*60*60*24);//计算天
//                long restTimes = totalTimes %(1000*60*60*24);//剩下的时间用于计于小时
//                long hourCount = restTimes/(1000*60*60);//小时
//                restTimes = restTimes % (1000*60*60);
//                long minuteCount = restTimes / (1000*60);
//
//                String spendTimes = dayCount+"天"+hourCount+"小时"+minuteCount+"分";
//                cc.setDuration(spendTimes);
//            }
//        }
//        IPage<TaskVO> bBpmProcessVoIPage = new Page<>();
//
//        bBpmProcessVoIPage.setRecords(page1.getRecords());
//        bBpmProcessVoIPage.setCurrent(page1.getCurrent());
//        bBpmProcessVoIPage.setSize(page1.getSize());
//        bBpmProcessVoIPage.setTotal(page1.getTotal());
//        return bBpmProcessVoIPage;
//    }
//
//    private String getCurrentName(String processInstanceId,Boolean flag,String processDefinitionId){
//        if(flag){
//            return "流程已结束";
//        }
//        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId).activityType("userTask").unfinished().orderByActivityInstanceStartTime().desc().list();
//        if(CollUtil.isEmpty(list)){
//            return "";
//        }
//        else{
//            String activityId = list.get(0).getActivityId();
//            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
//            FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(activityId);
//            return flowElement.getName();
//        }
//    }
//
//    private void collectUserTaskInfo(List<Comment> processInstanceComments,
//                                     List<Attachment> processInstanceAttachments,
//                                     HistoricActivityInstance historicActivityInstance,
//                                     List<TaskDetailVO> voList,
//                                     List<HistoricActivityInstance> activityInstanceList) {
//        for (HistoricActivityInstance activityInstance : activityInstanceList) {
//            TaskDetailVO taskDetailVO =new TaskDetailVO();
//            taskDetailVO.setTaskId(activityInstance.getTaskId());
//            taskDetailVO.setActivityId(activityInstance.getActivityId());
//            taskDetailVO.setName(activityInstance.getActivityName());
//            taskDetailVO.setCreateTime(activityInstance.getStartTime());
//            taskDetailVO.setEndTime(activityInstance.getEndTime());
//            Comment signComment = processInstanceComments.stream().filter(h -> h.getTaskId().equals(historicActivityInstance.getTaskId()) && h.getType().equals(SIGN_COMMENT)).findFirst().orElse(null);
//            if(signComment!=null){
//                taskDetailVO.setSignImage(signComment.getFullMessage());
//            }
//            List<Attachment> attachments = processInstanceAttachments.stream().filter(h -> h.getTaskId().equals(historicActivityInstance.getTaskId())).collect(Collectors.toList());
//            if(CollUtil.isNotEmpty(attachments)){
//                List<AttachmentVO> attachmentVOList = new ArrayList<>();
//                for (Attachment attachment : attachments) {
//                    AttachmentVO attachmentVO = new AttachmentVO();
//                    attachmentVO.setId(attachment.getId());
//                    attachmentVO.setName(attachment.getName());
//                    attachmentVO.setUrl(attachment.getUrl());
//                    attachmentVOList.add(attachmentVO);
//                }
//                taskDetailVO.setAttachmentVOList(attachmentVOList);
//            }
//
//            List<Comment> options = processInstanceComments.stream().filter(h -> h.getTaskId().equals(historicActivityInstance.getTaskId()) && h.getType().equals(OPINION_COMMENT)).collect(Collectors.toList());
//            if(CollUtil.isNotEmpty(options)){
//                List<OptionVO> optionVOList =new ArrayList<>();
//                for (Comment option : options) {
//                    OptionVO optionVO = new OptionVO();
//                    optionVO.setComments(option.getFullMessage());
//                    optionVO.setUserId(option.getUserId());
////                                        optionVO.setUserName();
//                    optionVO.setCreateTime(option.getTime());
//                    optionVOList.add(optionVO);
//                }
//                taskDetailVO.setOptionVOList(optionVOList);
//            }
//
//            List<Comment> comments = processInstanceComments.stream().filter(h -> h.getTaskId().equals(historicActivityInstance.getTaskId()) && h.getType().equals(COMMENTS_COMMENT)).collect(Collectors.toList());
//            if(CollUtil.isNotEmpty(comments)){
//                List<CommentVO> commentsVOList =new ArrayList<>();
//                for (Comment comment : comments) {
//                    CommentVO commentVO = new CommentVO();
//                    commentVO.setComments(comment.getFullMessage());
//                    commentVO.setUserId(comment.getUserId());
////                                        commentVO.setUserName();
//                    commentVO.setCreateTime(comment.getTime());
//                    commentsVOList.add(commentVO);
//                }
//                taskDetailVO.setCommentVOList(commentsVOList);
//            }
//
//            voList.add(taskDetailVO);
//
//
//
//        }
//    }
//
//}
