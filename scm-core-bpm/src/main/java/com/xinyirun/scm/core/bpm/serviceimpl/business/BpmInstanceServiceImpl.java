package com.xinyirun.scm.core.bpm.serviceimpl.business;

import cn.hutool.core.net.url.UrlBuilder;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.*;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import com.xinyirun.scm.core.bpm.mapper.business.*;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceService;
import com.xinyirun.scm.core.bpm.utils.PageUtil;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Log4j2
@Service
public class BpmInstanceServiceImpl extends ServiceImpl<BpmInstanceMapper, BpmInstanceEntity> implements IBpmInstanceService {

    @Autowired
    private BpmInstanceMapper mapper;

    @Autowired
    private BpmInstanceProcessMapper bpmInstanceProcessMapper;

    @Autowired
    private BpmTodoMapper bpmTodoMapper;

    @Autowired
    private BpmUsersMapper bpmUsersMapper;

    @Autowired
    @Lazy
    private HistoryService historyService;

    @Autowired
    @Lazy
    private TaskService taskService;

    @Autowired
    @Lazy
    private RuntimeService runtimeService;

    @Autowired
    private BpmInstanceApproveMapper bpmInstanceApproveMapper;

    @Autowired
    private BpmProcessTemplatesMapper bpmProcessTemplatesMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private int port;

    @Autowired
    private ApplicationContext ctx;

    /**
     * 保存待办任务数据 以及 更新流程节点信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskCreated(TaskEntity task) throws Exception {

        BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(task.getProcessInstanceId(), task.getProcessDefinitionId());
        // 创建第一条任务时候，事务未提交到数据库，不能对其进行操作
        if (bpmInstanceEntity != null && !task.getTaskDefinitionKey().equals("root")) {
            BpmTodoEntity todoEntity = bpmTodoMapper.selectByTaskId(bpmInstanceEntity.getCurrent_task_id());
            // 1.新增系统待办信息
            BpmTodoEntity bpmTodoEntity = new BpmTodoEntity();
            bpmTodoEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
            bpmTodoEntity.setTask_id(task.getId());
            bpmTodoEntity.setNode_id(task.getTaskDefinitionKey());
            bpmTodoEntity.setProcess_instance_id(task.getProcessInstanceId());
            bpmTodoEntity.setProcess_definition_id(task.getProcessDefinitionId());
            bpmTodoEntity.setTask_name(task.getName());
            bpmTodoEntity.setAssignee_code(task.getAssignee());
            bpmTodoEntity.setAssignee_name(bpmUsersMapper.selectByCode(task.getAssignee()).getUser_name());
            bpmTodoEntity.setForm_items(bpmInstanceEntity.getForm_items());
            bpmTodoEntity.setProcess(bpmInstanceEntity.getProcess());
            bpmTodoEntity.setSerial_id(bpmInstanceEntity.getSerial_id());
            bpmTodoEntity.setSerial_type(bpmInstanceEntity.getSerial_type());
            bpmTodoEntity.setDue_date(null);
            bpmTodoEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ZERO);
            bpmTodoEntity.setApprove_time(null);
            bpmTodoEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
            bpmTodoEntity.setRemark(null);
            bpmTodoEntity.setLast_todo_id(todoEntity != null ? todoEntity.getId() : null);
            bpmTodoMapper.insert(bpmTodoEntity);

            // 2.更新节点开始时间
            BpmInstanceProcessEntity bpmInstanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstanceAndStartTimeIsNull(task.getTaskDefinitionKey(), bpmInstanceEntity.getProcess_code());
            if (bpmInstanceProcessEntity!=null){
                bpmInstanceProcessEntity.setStart_time(LocalDateTime.now());
                bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_RUNNING);
                bpmInstanceProcessMapper.updateById(bpmInstanceProcessEntity);
            }

            // 3.更新用户操作信息
            BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selByNodeIdAndAssigneeCode(task.getTaskDefinitionKey(),bpmInstanceEntity.getProcess_code(), task.getAssignee());
            bpmInstanceApproveEntity.setTask_id(task.getId());
//            bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_RUNNING);
            bpmInstanceApproveMapper.updateById(bpmInstanceApproveEntity);

//            // 4.更新实例表 当前任务信息
//            bpmInstanceEntity.setCurrent_task_id(task.getId());
//            bpmInstanceEntity.setCurrent_task_name(task.getName());
//            mapper.updateById(bpmInstanceEntity);
            // 调用多次，数据变动更新
        }

        /**
         * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
         */
//        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());
//        if(bpmProcessTemplatesEntity.getCall_back_create_method()!=null){
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", bpmInstanceEntity.getSerial_id());
//            invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
//                    bpmProcessTemplatesEntity.getCall_back_create_method(),
//                    bpmProcessTemplatesEntity.getCall_back_create_param(),
//                    jsonObject);
//        }
    }

    /**
     * 审批流程实例完成事件
     */
    @Override
    public void processStarted(ProcessInstance processInstance){
        try {
            BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(processInstance.getProcessInstanceId(), processInstance.getProcessDefinitionId());
            BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", bpmInstanceEntity.getSerial_id());
            jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
            jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
            /**
             * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
             */
            invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
                    bpmProcessTemplatesEntity.getCall_back_create_method(),
                    bpmProcessTemplatesEntity.getCall_back_create_param(),
                    jsonObject);
        } catch (Exception e) {
            log.error("saveProcessInstanceCompleted error", e);
        }
    }


    /**
     * 更新待办任务状态为已处理 同意/拒绝
     *
     * @param task
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskCompleted(TaskEntity task) throws Exception {
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

        // 2.判断任务同意/拒绝(发起)
        if (variable != null && variable.equals(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE)) {
            saveTaskRefuse(task);
        } else {
            saveTaskAgree(task);
        }
    }

    /**
     * 同意任务
     */
    public void saveTaskAgree(TaskEntity task) throws Exception {
        BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(task.getProcessInstanceId(), task.getProcessDefinitionId());

        // 1.更新待办任务状态
        BpmTodoEntity todoEntity = bpmTodoMapper.selectByTaskId(task.getId());
        todoEntity.setApprove_time(LocalDateTime.now());
        todoEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        todoEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ONE);
        bpmTodoMapper.updateById(todoEntity);

        // 2.更新节点用户操作信息
        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
        bpmInstanceApproveEntity.setApprove_time(LocalDateTime.now());
        bpmInstanceApproveEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ONE);
        bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_AGREE);
        bpmInstanceApproveMapper.updateById(bpmInstanceApproveEntity);

        // 3.更新任务执行情况  1.查询当前节点的其他用户操作信息 2.会签全部完成 3.或签任一用户同意 '节点，删除其他用户待办任务，更新节点执行情况'
        List<BpmInstanceApproveEntity> bpmInstanceApproveEntities = bpmInstanceApproveMapper.selByNodeIdNotAssigneeCode(task.getTaskDefinitionKey(), bpmInstanceEntity.getProcess_code(), task.getAssignee(),DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_APPROVAL_MODE_AND);
        BpmInstanceProcessEntity bpmInstanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstance(task.getTaskDefinitionKey(), bpmInstanceEntity.getProcess_code());
        if (CollectionUtils.isEmpty(bpmInstanceApproveEntities)||bpmInstanceProcessEntity.getApproval_mode().equals(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_APPROVAL_MODE_OR)) {

            // 更新节点执行情况
            bpmInstanceProcessEntity.setFinish_time(LocalDateTime.now());
            bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_COMPLETE);
            bpmInstanceProcessMapper.updateById(bpmInstanceProcessEntity);

            // OR=或签
            if (StringUtils.isNotEmpty(bpmInstanceProcessEntity.getApproval_mode())
                    && bpmInstanceProcessEntity.getApproval_mode().equals(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_APPROVAL_MODE_OR)) {
                // 删除其他用户待办任务
                List<BpmTodoEntity> bpmTodoEntities = bpmTodoMapper.getProcessCodeAndNodeIdByStatusAll(bpmInstanceProcessEntity.getProcess_code(), bpmInstanceProcessEntity.getNode_id(), DictConstant.B_BPM_TODO_STATUS_ZERO);
                for (BpmTodoEntity bpmTodoEntity : bpmTodoEntities) {
                    bpmTodoMapper.deleteById(bpmTodoEntity.getId());
                }

                // 取消节点下的其他任务
                List<BpmInstanceApproveEntity> instanceApproveEntities = bpmInstanceApproveMapper.getProcessCodeAndNodeIdByStatusAll(bpmInstanceProcessEntity.getProcess_code(), bpmInstanceProcessEntity.getNode_id(), DictConstant.B_BPM_TODO_STATUS_ZERO);
                for (BpmInstanceApproveEntity instanceApproveEntity : instanceApproveEntities) {
                    instanceApproveEntity.setApprove_time(LocalDateTime.now());
                    instanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ELEVEN);
                    instanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_CANCEL);
                    bpmInstanceApproveMapper.updateById(instanceApproveEntity);
                }
            }

            // 4.更新下一个节点审批人或则办理人
            if (StringUtils.isNotEmpty(bpmInstanceApproveEntity.getIs_next())) {
                // 查询下一个节点审批人
                BBpmInstanceVo _vo =  bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApproveEntity.getProcess_code(), bpmInstanceApproveEntity.getIs_next());
                String nextApproveNames = _vo != null ? _vo.getNext_approve_name() : "";
                String nextApproveCodes = _vo != null ? _vo.getNext_approve_code() : "";
                BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());

                // 更新实例表下一个节点审批人
                if (StringUtils.isNotEmpty(nextApproveNames)){
                    bpmInstanceEntity.setNext_approve_name(nextApproveNames);
                    bpmInstanceEntity.setNext_approve_code(nextApproveCodes);
                    mapper.updateById(bpmInstanceEntity);
                }
                // 更新业务表下一个节点审批人
                if (StringUtils.isNotEmpty(bpmProcessTemplatesEntity.getCall_back_save_method()) && StringUtils.isNotEmpty(nextApproveNames)) {
                    // 配置更新审批人回调信息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", bpmInstanceEntity.getSerial_id());
                    jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
                    jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
                    jsonObject.put("next_approve_name", nextApproveNames);

                    /**
                     * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
                     */
                    invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
                            bpmProcessTemplatesEntity.getCall_back_save_method(),
                            bpmProcessTemplatesEntity.getCall_back_save_param(),
                            jsonObject);
                }

                // 更新业务表下一个节点审批人
//                if (StringUtils.isNotEmpty(bpmProcessTemplatesEntity.getCall_back_save_method_url()) && StringUtils.isNotEmpty(nextApproveNames)) {
//                    // 配置更新审批人回调信息
//                    JSONObject jsonObject = new JSONObject();
//                    ResponseEntity<JSONObject> response = null;
//                    String url = null;
//                    switch (bpmInstanceEntity.getSerial_type()) {
//                        case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE: // 企业
//                            jsonObject.put("id", bpmInstanceEntity.getSerial_id());
//                            jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
//                            jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
//                            jsonObject.put("next_approve_name", nextApproveNames);
//                            /**
//                             * 回调入口，TODO：servcie中调用controller，不合理，需要优化，可以直接调用外部的service
//                             */
//                            url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_save_method_url());
//                            response = restTemplate.postForEntity(url, jsonObject, JSONObject.class);
//                            log.debug("====》企业管理更新推送结果《====" + response);
//                            break;
//
//                        // 销售合同
//                        case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT:
//                            jsonObject.put("id", bpmInstanceEntity.getSerial_id());
//                            jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
//                            jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
//                            jsonObject.put("next_approve_name", nextApproveNames);
//
//                            url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_save_method_url());
//                            response = restTemplate.postForEntity(url, jsonObject, JSONObject.class);
//                            log.debug("====》销售合同更新推送结果《====" + response);
//                            break;
//                        // 采购合同
//                        case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CONTRACT:
//                            jsonObject.put("id", bpmInstanceEntity.getSerial_id());
//                            jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
//                            jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
//                            jsonObject.put("next_approve_name", nextApproveNames);
//
//                            url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_save_method_url());
//                            response = restTemplate.postForEntity(url, jsonObject, JSONObject.class);
//                            log.debug("====》采购合同更新推送结果《====" + response);
//                            break;
//                    }
//
//                }
            }
        }
    }

    /**
     * 反射调用
     * @param callBackClass
     * @param callBackMethod
     * @param callBackParam
     * @param jsonParams
     * @throws Exception
     */
    public void invokeCallback(String callBackClass, String callBackMethod, String callBackParam, JSONObject jsonParams) {
        try {
            // 加载Service类
            Class<?> serviceClass = Class.forName(callBackClass);
            Object serviceInstance = ctx.getBean(serviceClass);

            // 解析参数类型
            String[] paramTypeNames = callBackParam.split("\\s*,\\s*");
            Class<?>[] paramTypes = new Class<?>[paramTypeNames.length];
            for (int i = 0; i < paramTypeNames.length; i++) {
                paramTypes[i] = Class.forName(paramTypeNames[i]);
                log.debug("参数类型[{}]: {}", i, paramTypes[i].getName());
            }

            // 获取方法
            Method method = serviceClass.getMethod(callBackMethod, paramTypes);
            log.debug("找到回调方法: {}", method);

            // 转换参数
            ObjectMapper mapper = new ObjectMapper();
            Object[] args = new Object[paramTypes.length];
            if (paramTypes.length == 1) {
                // 打印JSON内容便于调试
                log.debug("转换JSON参数: {}", jsonParams);
                args[0] = mapper.convertValue(jsonParams, paramTypes[0]);
            } else {
                throw new IllegalArgumentException("不支持多参数调用: " + callBackMethod);
            }

            // 调用方法
            method.invoke(serviceInstance, args);
        } catch (ClassNotFoundException e) {
            log.error("找不到回调类: {}", callBackClass, e);
            throw new RuntimeException("回调类不存在: " + callBackClass, e);
        } catch (NoSuchMethodException e) {
            log.error("找不到回调方法: {}.{}", callBackClass, callBackMethod, e);
            throw new RuntimeException("回调方法不存在: " + callBackMethod, e);
        } catch (IllegalAccessException e) {
            log.error("无法访问回调方法: {}.{}", callBackClass, callBackMethod, e);
            throw new RuntimeException("无法访问回调方法", e);
        } catch (InvocationTargetException e) {
            log.error("回调方法执行异常: {}.{}", callBackClass, callBackMethod, e.getTargetException());
            throw new RuntimeException("回调方法执行失败", e.getTargetException());
        } catch (Exception e) {
            log.error("回调执行未知异常", e);
            throw new RuntimeException("回调执行失败", e);
        }
    }

    /**
     * 查看我发起的实例（流程）
     *
     * @param param
     */
    @Override
    public IPage<BBpmInstanceVo> selectPageList(BBpmInstanceVo param) {
        // 分页条件
        Page<BpmInstanceEntity> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());

        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        param.setOwner_code(SecurityUtil.getUserSession().getStaff_info().getCode());

        return mapper.selectPageList(pageCondition, param);
    }

    /**
     * 流程实例完成事件
     */
    @Override
    public void saveProcessInstanceCompleted(ProcessInstance processInstance) {
        try {
            // 1.更新流程实例状态
            BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(processInstance.getProcessInstanceId(), processInstance.getProcessDefinitionId());
            bpmInstanceEntity.setEnd_time(LocalDateTime.now());
            /** 已办结 */
            bpmInstanceEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_TWO);
            bpmInstanceEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
            mapper.updateById(bpmInstanceEntity);

            // 2.更新审批结果
            BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", bpmInstanceEntity.getSerial_id());
            jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
            jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
//            if (StringUtils.isNotEmpty(bpmProcessTemplatesEntity.getCall_back_approve_method_url())) {
//                String url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_approve_method_url());
//                ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, jsonObject, JSONObject.class);
//                log.debug("====》更新通过结果《====" + response);
//            } else {
//                log.debug("====》更新通过结果《====" + "审批通过回调接口为空");
//            }
            /**
             * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
             */
            invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
                    bpmProcessTemplatesEntity.getCall_back_approved_method(),
                    bpmProcessTemplatesEntity.getCall_back_approved_param(),
                    jsonObject);
        } catch (Exception e) {
            log.error("saveProcessInstanceCompleted error", e);
        }
    }

    /**
     * 组装url
     */
    protected String getBusinessCenterUrl(String uri) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();

            String url = UrlBuilder.create()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .addPath(uri)
                    .build();
            return url.replaceAll("%2F", "/");
        } catch (Exception e) {
            log.error("getBusinessCenterUrl error", e);
        }
        return "";
    }

    /**
     * 待办任务拒绝
     * @param task
     */
    @Override
    public void saveTaskCancelled(TaskEntity task) {
        // 更新待办任务状态
        BpmTodoEntity todoEntity = bpmTodoMapper.selectByTaskId(task.getId());
        todoEntity.setApprove_time(LocalDateTime.now());
        todoEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        todoEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_FOUR);
        bpmTodoMapper.updateById(todoEntity);

        // 拒绝后更新节点用户操作信息
        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
        bpmInstanceApproveEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        bpmInstanceApproveEntity.setApprove_time(LocalDateTime.now());
        bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_FOUR);
        bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_REFUSE);
        bpmInstanceApproveMapper.updateById(bpmInstanceApproveEntity);

        // 拒绝后更新节点执行情况
        BpmInstanceProcessEntity bpmInstanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstance(task.getTaskDefinitionKey(), bpmInstanceApproveEntity.getProcess_code());
        bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_REFUSE);
        bpmInstanceProcessMapper.updateById(bpmInstanceProcessEntity);
    }

    /**
     * 流程实例取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProcessInstanceCancelled(String taskId) throws Exception {
        // 拒绝后更新节点用户操作信息
        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(taskId);
        bpmInstanceApproveEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        bpmInstanceApproveEntity.setApprove_time(LocalDateTime.now());
        bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ELEVEN);
        bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_CANCEL);
        bpmInstanceApproveMapper.updateById(bpmInstanceApproveEntity);

        // 取消其他待办任务未待更新
        List<BpmInstanceApproveEntity> processCodeNotByTaskIdByStatus = bpmInstanceApproveMapper.getProcessCodeNotByTaskIdByStatus(bpmInstanceApproveEntity.getProcess_code(),taskId, DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
        for (BpmInstanceApproveEntity codeNotByTaskIdByStatus : processCodeNotByTaskIdByStatus) {
            codeNotByTaskIdByStatus.setApprove_time(LocalDateTime.now());
            codeNotByTaskIdByStatus.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ELEVEN);
            codeNotByTaskIdByStatus.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_CANCEL);
            bpmInstanceApproveMapper.updateById(codeNotByTaskIdByStatus);
        }

        // 拒绝后更新节点执行情况
        BpmInstanceProcessEntity bpmInstanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstance(bpmInstanceApproveEntity.getNode_id(), bpmInstanceApproveEntity.getProcess_code());
        bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_CANCEL);
        bpmInstanceProcessMapper.updateById(bpmInstanceProcessEntity);

        BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(bpmInstanceApproveEntity.getProcess_instance_id(), bpmInstanceApproveEntity.getProcess_definition_id());
        bpmInstanceEntity.setEnd_time(LocalDateTime.now());
        /** 已撤销 */
        bpmInstanceEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ONE);
        bpmInstanceEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        mapper.updateById(bpmInstanceEntity);

        // 删除所有用户待办任务
        List<BpmTodoEntity> bpmTodoEntities = bpmTodoMapper.getProcessCodeByStatusAll(bpmInstanceApproveEntity.getProcess_code(), DictConstant.B_BPM_TODO_STATUS_ZERO);
        for (BpmTodoEntity bpmTodoEntity : bpmTodoEntities) {
            bpmTodoMapper.deleteById(bpmTodoEntity.getId());
        }

        // 更新回调接口
        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", bpmInstanceEntity.getSerial_id());

//        if (StringUtils.isNotEmpty(bpmProcessTemplatesEntity.getCall_back_cancel_method_url())){
//            String url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_cancel_method_url());
//            ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, jsonObject,JSONObject.class);
//            log.debug("====》更新撤销结果《====" + response.getBody());
//        }else {
//            log.debug("====》更新撤销结果《====" + "审批撤销回调接口为空");
//        }

        /**
         * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
         */
        invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
                bpmProcessTemplatesEntity.getCall_back_cancel_method(),
                bpmProcessTemplatesEntity.getCall_back_cancel_param(),
                jsonObject);
    }

    /**
     * 审批流任务转交
     *
     * @param task
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskTransfer(TaskEntity task) throws Exception {

        // 获取任务受让人变量
        Object variable = runtimeService.getVariable(task.getProcessInstanceId(), WflowGlobalVarDef.TRANSFER_ASSIGNEE_CODE+task.getId());

        String taskId = task.getId();
        String assignee = variable.toString();
        BpmUsersEntity bpmUsersEntity = bpmUsersMapper.selectByCode(assignee);

        // 1.更新任务节点审批人
        BpmInstanceApproveEntity bpmInstanceApprove = bpmInstanceApproveMapper.selectByTaskId(taskId);
        bpmInstanceApprove.setAssignee_code(assignee);
        bpmInstanceApprove.setAssignee_name(bpmUsersEntity.getUser_name());
        bpmInstanceApproveMapper.updateById(bpmInstanceApprove);

        // 2.更新待办任务信息
        BpmTodoEntity todoEntity = bpmTodoMapper.selectByTaskId(taskId);
        todoEntity.setAssignee_code(assignee);
        todoEntity.setAssignee_name(bpmUsersEntity.getUser_name());
        bpmTodoMapper.updateById(todoEntity);

        BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(bpmInstanceApprove.getProcess_instance_id(), bpmInstanceApprove.getProcess_definition_id());

        // 4.更新下一个节点审批人或则办理人
        BBpmInstanceVo _vo = bpmInstanceApproveMapper.selNextApproveName(bpmInstanceApprove.getProcess_code(), bpmInstanceApprove.getNode_id());
        String nextApproveNames = _vo.getNext_approve_name();
        String nextApproveCodes = _vo.getNext_approve_code();
        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());

        // 更新实例表下一个节点审批人
        if (StringUtils.isNotEmpty(nextApproveNames)) {
            bpmInstanceEntity.setNext_approve_name(nextApproveNames);
            bpmInstanceEntity.setNext_approve_code(nextApproveCodes);
            mapper.updateById(bpmInstanceEntity);
        }

//        // 更新业务表下一个节点审批人
//        if (StringUtils.isNotEmpty(bpmProcessTemplatesEntity.getCall_back_save_method_url()) && StringUtils.isNotEmpty(nextApproveNames)) {
//            switch (bpmInstanceEntity.getSerial_type()) {
//                case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE: // 企业
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", bpmInstanceEntity.getSerial_id());
                    jsonObject.put("bpm_instance_id", bpmInstanceEntity.getId());
                    jsonObject.put("bpm_instance_code", bpmInstanceEntity.getProcess_code());
                    jsonObject.put("next_approve_name", nextApproveNames);
//
//                    String url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_save_method_url());
//                    ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, jsonObject, JSONObject.class);
//                    log.debug("====》更新推送结果《====" + response);
//                    break;
//            }
//        }
        /**
         * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
         */
        invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
                bpmProcessTemplatesEntity.getCall_back_save_method(),
                bpmProcessTemplatesEntity.getCall_back_save_param(),
                jsonObject);
    }

    /**
     * 拒绝任务
     */
    public void saveTaskRefuse(TaskEntity task) throws Exception {
        // 1.更新待办任务状态
        BpmTodoEntity todoEntity = bpmTodoMapper.selectByTaskId(task.getId());
        todoEntity.setApprove_time(LocalDateTime.now());
        todoEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        todoEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_FOUR);
        bpmTodoMapper.updateById(todoEntity);

        // 2.拒绝后更新节点用户操作信息
        BpmInstanceApproveEntity bpmInstanceApproveEntity = bpmInstanceApproveMapper.selectByTaskId(task.getId());
        bpmInstanceApproveEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
        bpmInstanceApproveEntity.setApprove_time(LocalDateTime.now());
        bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_FOUR);
        bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE);
        bpmInstanceApproveMapper.updateById(bpmInstanceApproveEntity);

        // 3.取消其他待办任务待更新
        List<BpmInstanceApproveEntity> processCodeNotByTaskIdByStatus = bpmInstanceApproveMapper.getProcessCodeNotByTaskIdByStatus(bpmInstanceApproveEntity.getProcess_code(), task.getId(), DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
        for (BpmInstanceApproveEntity codeNotByTaskIdByStatus : processCodeNotByTaskIdByStatus) {
            codeNotByTaskIdByStatus.setApprove_time(LocalDateTime.now());
            codeNotByTaskIdByStatus.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ELEVEN);
            codeNotByTaskIdByStatus.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_CANCEL);
            bpmInstanceApproveMapper.updateById(codeNotByTaskIdByStatus);
        }

        // 4.拒绝后更新节点执行情况
        BpmInstanceProcessEntity bpmInstanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstance(bpmInstanceApproveEntity.getNode_id(), bpmInstanceApproveEntity.getProcess_code());
        bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_REFUSE);
        bpmInstanceProcessMapper.updateById(bpmInstanceProcessEntity);

        BpmInstanceEntity bpmInstanceEntity = mapper.selectByInstanceIdAndDefId(bpmInstanceApproveEntity.getProcess_instance_id(), bpmInstanceApproveEntity.getProcess_definition_id());
        bpmInstanceEntity.setEnd_time(LocalDateTime.now());
        /** 审批驳回 */
        bpmInstanceEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_THREE);
        bpmInstanceEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        mapper.updateById(bpmInstanceEntity);

        // 5.删除未办任务
        List<BpmTodoEntity> bpmTodoEntities = bpmTodoMapper.getProcessCodeNotByTaskIdByStatus(bpmInstanceApproveEntity.getProcess_code(), task.getId(),DictConstant.B_BPM_TODO_STATUS_ZERO);
        for (BpmTodoEntity bpmTodoEntity : bpmTodoEntities) {
            bpmTodoMapper.deleteById(bpmTodoEntity.getId());
        }

        // 6.更新回调接口改变状态
        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = bpmProcessTemplatesMapper.selectById(bpmInstanceEntity.getProcess_id());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", bpmInstanceEntity.getSerial_id());

//        if (StringUtils.isNotEmpty(bpmProcessTemplatesEntity.getCall_back_refuse_method_url())) {
//            String url = getBusinessCenterUrl(bpmProcessTemplatesEntity.getCall_back_refuse_method_url());
//            ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, jsonObject, JSONObject.class);
//            log.debug("====》更新驳回结果《====" + response.getBody());
//        } else {
//            log.debug("====》更新驳回结果《====" + "驳回回调接口为空");
//        }

        /**
         * 回调入口，servcie中调用controller，不合理，需要优化，可以直接调用外部的service --已完成
         */
        invokeCallback(bpmProcessTemplatesEntity.getCall_back_class(),
                bpmProcessTemplatesEntity.getCall_back_refuse_method(),
                bpmProcessTemplatesEntity.getCall_back_refuse_param(),
                jsonObject);
    }

}
