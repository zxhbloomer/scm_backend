package com.xinyirun.scm.core.bpm.serviceimpl.business;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xinyirun.scm.bean.bpm.dto.json.ChildNode;
import com.xinyirun.scm.bean.bpm.enums.NodeTypeEnum;
import com.xinyirun.scm.bean.bpm.vo.ProcessNode;
import com.xinyirun.scm.bean.bpm.vo.form.Form;
import com.xinyirun.scm.bean.bpm.vo.props.ApprovalProps;
import com.xinyirun.scm.bean.entity.bpm.*;
import com.xinyirun.scm.bean.entity.business.allocate.BAllocateOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.*;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.bpm.WorkFlowConstants;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import com.xinyirun.scm.core.bpm.mapper.business.*;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessTemplatesService;
import com.xinyirun.scm.core.bpm.service.business.IBpmUsersService;
import com.xinyirun.scm.core.bpm.service.business.ProcessNodeCatchService;
import com.xinyirun.scm.core.bpm.serviceimpl.common.autocode.BpmInstanceAutoCodeServiceImpl;
import com.xinyirun.scm.core.bpm.serviceimpl.common.autocode.BpmProcessTemplatesAutoCodeServiceImpl;
import com.xinyirun.scm.core.bpm.utils.PageUtil;
import com.xinyirun.scm.core.bpm.utils.WFlowToBpmnCreator;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * process_templates 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@Slf4j
@Service("iBpmProcessTemplatesService")
public class BpmProcessTemplatesServiceImpl extends ServiceImpl<BpmProcessTemplatesMapper, BpmProcessTemplatesEntity> implements IBpmProcessTemplatesService {

    //超时缓存，数据缓存20秒，用来存储审批人防止flowable高频调用
    private static final TimedCache<String, List<String>> taskCache = CacheUtil.newTimedCache(20000);


    @Autowired
    private BpmProcessTemplatesMapper mapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private BpmFormGroupsMapper bpmFormGroupsMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmInstanceAutoCodeServiceImpl bpmInstanceAutoCodeService;

    @Autowired
    private BpmProcessTemplatesAutoCodeServiceImpl bpmProcessTemplatesAutoCodeService;

    @Autowired
    private BpmTodoMapper bpmTodoMapper;

    @Autowired
    private IBpmUsersService bpmUsersService;

    @Autowired
    private BpmInstanceProcessMapper bpmInstanceProcessMapper;

    @Autowired
    private BpmInstanceMapper bpmInstanceMapper;

    @Autowired
    private BpmInstanceApproveMapper bpmInstanceApproveMapper;

    @Override
    public IPage<BBpmProcessVo> selectPage(BBpmProcessVo searchCondition) {
        // 分页条件
        Page<BAllocateOrderEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取详情
     */
    @Override
    public BBpmProcessVo selectById(Integer id) {
        BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = mapper.selectById(id);
        BeanUtilsSupport.copyProperties(bpmProcessTemplatesEntity, bBpmProcessVo);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(WorkFlowConstants.PROCESS_PREFIX + bpmProcessTemplatesEntity.getTemplate_id()).latestVersion().singleResult();
//        if(processDefinition == null){
//            throw new BusinessException("该流程暂未接入Flowable,请重试");
//        }
        bBpmProcessVo.setProcess_definition_id(processDefinition == null ? null : processDefinition.getId());
        return bBpmProcessVo;
    }

    /**
     * 获取模板分组
     */
    @Override
    public List<BBpmGroupVo> getGroup() {
        return bpmFormGroupsMapper.getGroup();
    }

    /**
     * 模板发布
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BBpmProcessVo> deployBom(BBpmProcessVo param) {
        /** 定义bean，部署发布的流程流程致为新流程，version+1 */
        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = mapper.selectById(param.getId());
        if (ObjectUtil.isEmpty(bpmProcessTemplatesEntity)) {
            return null;
        }

        /** 定义bean，更新原流程致不可用 */
        BpmProcessTemplatesEntity oldEntity = new BpmProcessTemplatesEntity();
        BeanUtilsSupport.copyProperties(bpmProcessTemplatesEntity, oldEntity);
        oldEntity.setIs_stop(Boolean.TRUE);
        mapper.updateById(oldEntity);

        /**
         * 以下为新增逻辑
         */
        BpmProcessNodeVo<?> processNode = JSONObject.parseObject(param.getProcess(), BpmProcessNodeVo.class);
        BpmnModel bpmnModel = new WFlowToBpmnCreator().loadBpmnFlowXmlByProcess(WflowGlobalVarDef.FLOWABLE+bpmProcessTemplatesEntity.getTemplate_id(), bpmProcessTemplatesEntity.getName(), processNode);

        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
        // 验证失败信息的封装ValidationError
        List<ValidationError> validate = defaultProcessValidator.validate(bpmnModel);
        if (CollectionUtil.isNotEmpty(validate)) {
            log.error("流程[{}验证失败]：{}", JSONObject.toJSONString(validate));
            throw new BusinessException("流程设计错误:" + validate.stream()
                    .map(err -> (err.getActivityId() + ":" + err.getActivityName()))
                    .collect(Collectors.joining(",")));
        }
        String xmlString = new String(new BpmnXMLConverter().convertToXML(bpmnModel));
        //  流程部署
        log.debug("流程生成bpmn-xml为：{}", xmlString);

        Deployment deploy = repositoryService.createDeployment()
                .name(param.getName())
                .tenantId("default")
                .addString(WflowGlobalVarDef.FLOWABLE + param.getTemplate_id() + WflowGlobalVarDef.BPMN, xmlString)
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        bpmProcessTemplatesEntity.setName(param.getName());
        bpmProcessTemplatesEntity.setProcess(param.getProcess());
        bpmProcessTemplatesEntity.setSettings(param.getSettings());
        bpmProcessTemplatesEntity.setRemark(param.getRemark());
        bpmProcessTemplatesEntity.setGroup_id(param.getGroup_id());
        bpmProcessTemplatesEntity.setDeployment_id(processDefinition.getId());
        /**
         * 部署审批流程，不是更新，是新增，并且version增加1
         * 并且把之前的流程设置为不可用
         */
        bpmProcessTemplatesEntity.setVersion(bpmProcessTemplatesEntity.getVersion()+1);
        bpmProcessTemplatesEntity.setId(null);
//        mapper.updateById(bpmProcessTemplatesEntity);
        bpmProcessTemplatesEntity.setCode(bpmProcessTemplatesAutoCodeService.autoCode().getCode());
        mapper.insert(bpmProcessTemplatesEntity);
        /**
         * 重新查询，返回给前端
         */
        BBpmProcessVo rtn = selectById(bpmProcessTemplatesEntity.getId());

        //        //解析配置的权限，进行拆分
//        reloadModelsPerm(code, processNode);
//        //部署流程就是从历史表提取最新的流程
//        log.info("部署流程{}成功，ID={}:", code, deploy.getId());
        return UpdateResultUtil.OK(rtn);
    }

    /**
     * 审批流程数据
     *
     * @param param 包含审批流程参数的对象
     * @return 包含审批流程数据的对象
     */
    @Override
    public BBpmProcessVo getBpmFlow(BBpmProcessVo param) {
        String code = null;
        String userCode = SecurityUtil.getUserSession().getStaff_info().getCode();
        String userId = SecurityUtil.getStaff_id().toString();
        String userName = SecurityUtil.getUserSession().getStaff_info().getName();
        String userAvatar = SecurityUtil.getLoginUserEntity().getAvatar();

//        // 1.根据code获取流程模板
//        switch (param.getSerial_type()){
//            // 出库计划
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_B_OUT_PLAN;
//                break;
//            // 企业管理
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_M_ENTERPRISE;
//                break;
//            // 项目管理
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PROJECT:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_B_PROJECT;
//                break;
//            // 采购合同
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CONTRACT:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_B_PO_CONTRACT;
//                break;
//            // 销售合同
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_B_SO_CONTRACT;
//                break;
//            default:
//               return null;
//        }

        // 2.根据type获取流程模板
        BBpmProcessVo bBpmProcessVo = mapper.getBpmFLowByType(param.getSerial_type());
        if (bBpmProcessVo==null){
            return null;
        }

        // 3.判断当前登录用户是否在发起审批流名单内
        OrgUserVo userInfo = new OrgUserVo();
        List<OrgUserVo> orgUserVoList = JSONArray.parseArray(bBpmProcessVo.getOrgUserVoList().toString(), OrgUserVo.class);
        if (CollectionUtil.isNotEmpty(orgUserVoList)){
            Optional<OrgUserVo> optional = orgUserVoList.stream().filter(k -> k.getCode().equals(userCode)).findFirst();
            // 用户不在发起审批流名单内，无法发起审批流弹窗
            if (!optional.isPresent()){
                return null;
            }
            userInfo = optional.get();
        }else {
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setCode(userCode);
            userInfo.setAvatar(userAvatar);
        }

        userInfo.setType("ROOT");
        bBpmProcessVo.setOrgUserVo(userInfo);

        // 审批流程定义id
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(WorkFlowConstants.PROCESS_PREFIX + bBpmProcessVo.getTemplate_id()).latestVersion().singleResult();
        bBpmProcessVo.setProcess_definition_id(processDefinition == null ? null : processDefinition.getId());

        return bBpmProcessVo;
    }

    /**
     * 获取审批流程数据
     * @param type
     * @return
     */
    @Override
    public String getBpmFLowCodeByType(String type) {
        BBpmProcessVo bBpmProcessVo = mapper.getBpmFLowByType(type);
        return bBpmProcessVo.getCode();
    }
    /**
     * 获取app审批流程模型数据
     * @param param
     * @return
     */
    @Override
    public AppBBpmProcessJson getAppProcessModel(BBpmProcessVo param) {
        String process = getAppFlow(param).getProcess();
        ProcessNode<?> root = reloadProcessByStr(process).get("root");
        AppBBpmProcessJson rtn = new AppBBpmProcessJson();
        rtn.setRoot(root);
        AppStaffUserBpmInfoVo _param = new AppStaffUserBpmInfoVo();
        _param.setId(SecurityUtil.getStaff_id());
        rtn.setAppStaffVo(getBpmDataByStaffid(_param));
        return rtn;
    }

    /**
     * 获取staffvo
     * @param id
     * @return
     */
    public MStaffVo selectMstaffVoByid(Long id){
        MStaffVo searchCondition = new MStaffVo();
        searchCondition.setId(id);
        MStaffVo vo = mapper.selectMstaffVoByid(searchCondition);
        return vo;
    }

    /**
     * 获取userVo
     * @param id
     * @return
     */
    public MUserVo selectUserById(Long id) {
        return mapper.selectUserById(id);
    }

    /**
     * 获取app可使用的审批流数据
     * @param param
     * @return
     */
    public BBpmProcessVo getAppFlow(BBpmProcessVo param) {
        String code = null;

        Long _userId = SecurityUtil.getStaff_id();
        String userId = _userId.toString();
        MStaffVo mStaffVo = selectMstaffVoByid(_userId);

        String userCode = mStaffVo.getCode();
        String userName = mStaffVo.getName();
        String userAvatar = selectUserById(mStaffVo.getUser_id()).getAvatar();

//        // 1.根据code获取流程模板
//        switch (param.getSerial_type()){
//            // 出库计划
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_B_OUT_PLAN;
//                break;
//            // 企业管理
//            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE:
//                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_M_ENTERPRISE;
//                break;
//            default:
//                return null;
//        }

        // 2.根据type获取流程模板
        BBpmProcessVo appProcessVo = mapper.getBpmFLowByType(param.getSerial_type());
        if (appProcessVo==null){
            return null;
        }

        // 3.判断当前登录用户是否在发起审批流名单内
        OrgUserVo userInfo = new OrgUserVo();
        List<OrgUserVo> orgUserVoList = JSONArray.parseArray(appProcessVo.getOrgUserVoList().toString(), OrgUserVo.class);
        if (CollectionUtil.isNotEmpty(orgUserVoList)){
            Optional<OrgUserVo> optional = orgUserVoList.stream().filter(k -> k.getCode().equals(userCode)).findFirst();
            // 用户不在发起审批流名单内，无法发起审批流弹窗
            if (!optional.isPresent()){
                return null;
            }
            userInfo = optional.get();
        }else {
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setCode(userCode);
            userInfo.setAvatar(userAvatar);
        }

        userInfo.setType("ROOT");
        appProcessVo.setOrgUserVo(userInfo);

        // 审批流程定义id
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(WorkFlowConstants.PROCESS_PREFIX + appProcessVo.getTemplate_id()).latestVersion().singleResult();
        appProcessVo.setProcess_definition_id(processDefinition == null ? null : processDefinition.getId());

        return appProcessVo;
    }    /**
     * 获取审批节点使用的数据
     * @param vo
     * @return
     */
    @Override
    public AppStaffUserBpmInfoVo getBpmDataByStaffid(AppStaffUserBpmInfoVo vo) {
        return mapper.getBpmDataByStaffid(vo.getId());
    }

    /**
     * 根据页面代码获取BPM数据
     * @param param
     * @return
     */
    @Override
    public List<BBpmProcessVo> getBpmDataByPageCode(BBpmProcessVo param) {
        return mapper.getBpmDataByPageCode(param);
    }

    /**
     * 根据字符串重新加载流程，将 JSON 格式的流程描述转换为流程节点的映射表。
     *
     * @param process JSON 格式的流程字符串
     * @return 流程节点的映射表，其中键为节点 ID，值为对应的流程节点对象
     */
    public Map<String, ProcessNode<?>> reloadProcessByStr(String process) {
        // 创建一个有序的 LinkedHashMap 用于存储流程节点的映射
        Map<String, ProcessNode<?>> nodeMap = new LinkedHashMap<>();

        // 将 JSON 格式的流程字符串解析为 ProcessNode 对象，并递归加载节点到映射表
        loadProcess(JSONObject.parseObject(process, ProcessNode.class), nodeMap);

        // 返回加载后的流程节点映射表
        return nodeMap;
    }

    /**
     * 递归加载流程节点，将节点及其子节点添加到节点映射表中。
     *
     * @param node    当前处理的流程节点
     * @param nodeMap 节点映射表，存储所有节点，键为节点 ID，值为对应的节点对象
     */
    private void loadProcess(ProcessNode<?> node, Map<String, ProcessNode<?>> nodeMap) {
        // 如果节点不为空且节点 ID 存在
        if (null != node && null != node.getId()) {
            // 调用coverProps 方法，对节点属性进行转换或初始化
            coverProps(node);

            // 将当前节点添加到节点映射表中
            nodeMap.put(node.getId(), node);

            // 如果节点类型为并行（CONCURRENTS）或条件（CONDITIONS），递归加载分支节点
            if (NodeTypeEnum.CONCURRENTS.equals(node.getType()) || NodeTypeEnum.CONDITIONS.equals(node.getType())) {
                node.getBranchs().forEach(n -> loadProcess(n, nodeMap));
            }

            // 递归加载当前节点的子节点
            loadProcess(node.getChildren(), nodeMap);
        }
    }

    /**
     * 节点props属性强制转换
     * @param node 节点
     */
    public synchronized static void coverProps(ProcessNode<?> node){
        if (node.getType().getTypeClass() != Object.class){
            node.setProps(((JSONObject)node.getProps()).toJavaObject((Type) node.getType().getTypeClass()));
        }
    }

    @Autowired
    private ProcessNodeCatchService nodeCatchService;

    /**
     * 通过流程定义id启动流程
     */
    @Override
    public void startProcess(BBpmProcessVo bBpmProcessVo) {

        // 1.校验流程启动参数
        checkbBpmProcessVo(bBpmProcessVo);

        // 2.初始化流程
        Task task = initialProcess(bBpmProcessVo);

        // 3.发起人任务自动完成
        Map<String, Object> var = new HashMap<>();
        var.put(WflowGlobalVarDef.APPROVE + task.getId(), BpmProcessHandlerParamsVo.Action.agree);
        taskService.complete(task.getId(), var);

    }

    /**
     * 初始化流程
     */
    @Transactional(rollbackFor = Exception.class)
    public Task initialProcess(BBpmProcessVo bBpmProcessVo) {
        // 根据code获取流程模板
        BpmProcessTemplatesEntity bpmProcessTemplates = mapper.selectByCode(bBpmProcessVo.getCode());

        // 组装流程变量
        Map<String, Object> processVar = new HashMap<>();
        processVar.putAll(bBpmProcessVo.getForm_data());
        processVar.putAll(bBpmProcessVo.getProcess_users());

        processVar.put("owner", JSONObject.toJSONString(bBpmProcessVo.getOrgUserVo()));
        Map<String, BpmProcessNodeVo<?>> nodeMap = nodeCatchService.reloadProcessByStr(bpmProcessTemplates.getProcess());
        Map<String, Object> propsMap = nodeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                v -> null == v.getValue().getProps() ? new HashMap<>() : v.getValue().getProps()));
        //将表单及流程配置设置为变量，跟随版本
        processVar.put(WflowGlobalVarDef.WFLOW_NODE_PROPS, propsMap);
        processVar.put(WflowGlobalVarDef.WFLOW_FORMS, JSONArray.parseArray(bpmProcessTemplates.getForm_items(), Form.class));
        processVar.put(WflowGlobalVarDef.INITIATOR, bBpmProcessVo.getOrgUserVo().getCode());
        //这样做貌似无效果，变量表不会多INITIATOR变量，但是流程表发起人有效
        Authentication.setAuthenticatedUserId(bBpmProcessVo.getOrgUserVo().getCode());

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(bpmProcessTemplates.getDeployment_id(), processVar);
        log.info("启动 {} 流程实例 {} 成功", processInstance.getProcessDefinitionName(), processInstance.getProcessInstanceId());

        // 第一个任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        // 初始化节点数据
        insertInstanceEntity(bBpmProcessVo, task, processInstance, bpmProcessTemplates);

        return task;
    }

    private void checkbBpmProcessVo(BBpmProcessVo bBpmProcessVo) {
        if (ObjectUtil.isEmpty(bBpmProcessVo.getCode())){
            log.error("CODE不能为空");
            throw new BusinessException("CODE不能为空");
        }
        if (ObjectUtil.isEmpty(bBpmProcessVo.getSerial_id())){
            log.error("业务ID不能为空");
            throw new BusinessException("业务ID不能为空");
        }
        if (ObjectUtil.isEmpty(bBpmProcessVo.getForm_json())){
            log.error("业务初始数据不能为空");
            throw new BusinessException("业务初始数据不能为空");
        }
        if (ObjectUtil.isEmpty(bBpmProcessVo.getOrgUserVo())){
            log.error("初始人数据不能为空");
            throw new BusinessException("初始人数据不能为空");
        }
        if (ObjectUtil.isEmpty(bBpmProcessVo.getInitial_process())){
            log.error("流程数据不能为空");
            throw new BusinessException("流程数据不能为空");
        }
    }


    /**
     * 校验审批流程表单数据
     */
    @Override
    public JSONObject checkFormItem(String code,Map mapVo) {
        JSONObject object = new JSONObject();

        BpmProcessTemplatesEntity bpmProcessTemplatesEntity = mapper.selectByCode(code);
        List<BpmProcessFormItemsVo> bpmProcessFormItemsVos = JSONArray.parseArray(bpmProcessTemplatesEntity.getForm_items(),BpmProcessFormItemsVo.class);
        if (bpmProcessFormItemsVos== null || bpmProcessFormItemsVos.size() == 0){
            return null;
        }

        bpmProcessFormItemsVos.stream().forEach(k->{
            if (k.getProps().isRequired() && mapVo.get(k.getId()) == null) {
                throw new BusinessException(k.getTitle() + "不能为空");
            }
            object.put(k.getId(),mapVo.get(k.getId()) == null ? null : mapVo.get(k.getId()));
        });

        return object;
    }

    /**
     * 插入实例实体
     */
    public BpmInstanceEntity insertInstanceEntity(BBpmProcessVo param, Task task, ProcessInstance processInstance,BpmProcessTemplatesEntity bpmProcessTemplatesEntity) {

        // 1.保存流程实例
        BpmInstanceEntity bpmInstanceEntity = new BpmInstanceEntity();
        bpmInstanceEntity.setProcess_code(bpmInstanceAutoCodeService.autoCode().getCode());
        bpmInstanceEntity.setProcess_instance_id(processInstance.getProcessInstanceId());
        bpmInstanceEntity.setProcess_definition_id(processInstance.getProcessDefinitionId());
        bpmInstanceEntity.setProcess_definition_name(processInstance.getProcessDefinitionName());
        bpmInstanceEntity.setProcess_definition_version(bpmProcessTemplatesEntity.getVersion());
        bpmInstanceEntity.setBusiness_key(JSONObject.toJSONString(param.getForm_data()));
        bpmInstanceEntity.setForm_data(JSONObject.toJSONString(param.getForm_data()));
        bpmInstanceEntity.setForm_json(JSONObject.toJSONString(param.getForm_json()));
        bpmInstanceEntity.setForm_class(param.getForm_class());
        bpmInstanceEntity.setOwner_code(param.getOrgUserVo().getCode());
        bpmInstanceEntity.setOwner_name(param.getOrgUserVo().getName());
        bpmInstanceEntity.setStart_time(LocalDateTime.ofInstant(processInstance.getStartTime().toInstant(), ZoneId.systemDefault()));
        bpmInstanceEntity.setEnd_time(null);
        bpmInstanceEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
        bpmInstanceEntity.setForm_items(bpmProcessTemplatesEntity.getForm_items());
        bpmInstanceEntity.setProcess(bpmProcessTemplatesEntity.getProcess());
        bpmInstanceEntity.setInitial_process(param.getInitial_process());
        bpmInstanceEntity.setSerial_id(param.getSerial_id());
        bpmInstanceEntity.setSerial_type(param.getSerial_type());
        bpmInstanceEntity.setCurrent_task_id(task.getId());
        bpmInstanceEntity.setCurrent_task_name(task.getName());
        bpmInstanceEntity.setProcess_id(bpmProcessTemplatesEntity.getId());
        // 当前节点处理人
        bpmInstanceEntity.setNext_approve_name(param.getOrgUserVo().getName());
        bpmInstanceEntity.setNext_approve_code(param.getOrgUserVo().getCode());
        int instanceSign = bpmInstanceMapper.insert(bpmInstanceEntity);
        if (instanceSign == 0){
            throw new BusinessException("流程实例保存失败");
        }

        // 2.保存节点信息
        //JSONObject process = JSONObject.parseObject(param.getInitial_process());
        //ChildNode childNode = JSONObject.parseObject(process.toJSONString(), new TypeReference<ChildNode>() {});
        //List<BpmInstanceProcessEntity> instanceProcessEntities = new ArrayList<>();
        //List<BpmInstanceApproveEntity> instanceApproveEntities = new ArrayList<>();
        //processNode(param.getOrgUserVo(),instanceProcessEntities,instanceApproveEntities,childNode,bpmInstanceEntity,task);

        List<BpmChildNodeVo> bpmChildNodeVos = JSONArray.parseArray(param.getInitial_process(),BpmChildNodeVo.class);
        List<BpmInstanceProcessEntity> instanceProcessEntities = new ArrayList<>();
        List<BpmInstanceApproveEntity> instanceApproveEntities = new ArrayList<>();
        newProcessNode(param.getOrgUserVo(),instanceProcessEntities,instanceApproveEntities,bpmChildNodeVos,bpmInstanceEntity,task);

        // 保存节点信息
        instanceProcessEntities.stream().forEach(k->{
            int instanceProcessSign = bpmInstanceProcessMapper.insert(k);
            if (instanceProcessSign == 0){
                throw new BusinessException("流程实例保存失败");
            }
        });

        // 保存审批人，抄送人，办理人信息
        instanceApproveEntities.stream().forEach(k->{
            int instanceApproveSign = bpmInstanceApproveMapper.insert(k);
            if (instanceApproveSign == 0){
                throw new BusinessException("流程实例保存失败");
            }
        });

        // 3.创建第一个代办任务信息（由于事务未提交，监听器无法创建第一条任务代办）
        BpmTodoEntity bpmTodoEntity = new BpmTodoEntity();
        bpmTodoEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
        bpmTodoEntity.setTask_id(task.getId());
        bpmTodoEntity.setNode_id("root");
        bpmTodoEntity.setProcess_instance_id(task.getProcessInstanceId());
        bpmTodoEntity.setProcess_definition_id(task.getProcessDefinitionId());
        bpmTodoEntity.setTask_name(task.getName());
        bpmTodoEntity.setAssignee_code(task.getAssignee());
        bpmTodoEntity.setAssignee_name(bpmUsersService.selectByCode(task.getAssignee()).getUser_name());
        bpmTodoEntity.setForm_items(bpmInstanceEntity.getForm_items());
        bpmTodoEntity.setProcess(bpmInstanceEntity.getProcess());
        bpmTodoEntity.setSerial_id(bpmInstanceEntity.getSerial_id());
        bpmTodoEntity.setSerial_type(bpmInstanceEntity.getSerial_type());
        bpmTodoEntity.setDue_date(null);
        bpmTodoEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
        bpmTodoEntity.setApprove_time(null);
        bpmTodoEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
        bpmTodoEntity.setRemark(null);
        bpmTodoEntity.setLast_todo_id(null);
        bpmTodoMapper.insert(bpmTodoEntity);

        return bpmInstanceEntity;
    }

    // 封装节点信息
    public void processNode(OrgUserVo orgUserVo, List<BpmInstanceProcessEntity> instanceProcessEntities, List<BpmInstanceApproveEntity> instanceApproveEntities, ChildNode childNode, BpmInstanceEntity bpmInstanceEntity, Task task) {
        // 1.增加第一个节点信息保存任务id
        BpmInstanceProcessEntity bpmInstanceProcessEntity = new BpmInstanceProcessEntity();
        bpmInstanceProcessEntity.setNode_id(childNode.getId());
        bpmInstanceProcessEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
        bpmInstanceProcessEntity.setIs_next(childNode.getChildren()!=null?childNode.getChildren().getId():null);
        bpmInstanceProcessEntity.setApproval_mode(childNode.getProps()!=null&&childNode.getProps().getMode()!=null?childNode.getProps().getMode():null);
        bpmInstanceProcessEntity.setNode_type(childNode.getType());
        bpmInstanceProcessEntity.setName(childNode.getName());
        bpmInstanceProcessEntity.setOwner_code(orgUserVo.getCode());
        bpmInstanceProcessEntity.setOwner_name(orgUserVo.getName());
        bpmInstanceProcessEntity.setAction(null);
        bpmInstanceProcessEntity.setComment_id(null);
        bpmInstanceProcessEntity.setStart_time(task!=null?LocalDateTime.now():null);
        bpmInstanceProcessEntity.setFinish_time(null);
        instanceProcessEntities.add(bpmInstanceProcessEntity);

        // 生成所有节点用户信息
        if (task != null && childNode.getType().equals("ROOT")) {
            BpmInstanceApproveEntity bpmInstanceApproveEntity = new BpmInstanceApproveEntity();
            bpmInstanceApproveEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
            bpmInstanceApproveEntity.setTask_id(task.getId());
            bpmInstanceApproveEntity.setType(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_ONE);
            bpmInstanceApproveEntity.setNode_id(childNode.getId());
            bpmInstanceApproveEntity.setProcess_instance_id(bpmInstanceEntity.getProcess_instance_id());
            bpmInstanceApproveEntity.setProcess_definition_id(bpmInstanceEntity.getProcess_definition_id());
            bpmInstanceApproveEntity.setTask_name(task.getName());
            bpmInstanceApproveEntity.setAssignee_code(task.getAssignee());
            bpmInstanceApproveEntity.setAssignee_name(bpmUsersService.selectByCode(task.getAssignee()).getUser_name());
            bpmInstanceApproveEntity.setForm_items(bpmInstanceEntity.getForm_items());
            bpmInstanceApproveEntity.setProcess(bpmInstanceEntity.getProcess());
            bpmInstanceApproveEntity.setSerial_id(bpmInstanceEntity.getSerial_id());
            bpmInstanceApproveEntity.setSerial_type(bpmInstanceEntity.getSerial_type());
            bpmInstanceApproveEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
            bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
            bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_RUNNING);
            bpmInstanceApproveEntity.setIs_next(childNode.getChildren()!=null?childNode.getChildren().getId():null);
            bpmInstanceApproveEntity.setRemark(null);
            bpmInstanceApproveEntity.setDue_date(null);
            bpmInstanceApproveEntity.setApprove_time(null);
            instanceApproveEntities.add(bpmInstanceApproveEntity);
        } else if (childNode.getProps().getAssignedUser()!=null){
            // 生成所有用户操作节点信息
            childNode.getProps().getAssignedUser().forEach(k->{
                BpmInstanceApproveEntity bpmInstanceApproveEntity = new BpmInstanceApproveEntity();
                bpmInstanceApproveEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
                bpmInstanceApproveEntity.setTask_id(null);
                bpmInstanceApproveEntity.setNode_id(childNode.getId());
                bpmInstanceApproveEntity.setProcess_instance_id(bpmInstanceEntity.getProcess_instance_id());
                bpmInstanceApproveEntity.setProcess_definition_id(bpmInstanceEntity.getProcess_definition_id());
                bpmInstanceApproveEntity.setTask_name(childNode.getName());
                bpmInstanceApproveEntity.setType(childNode.getType().equals("CC")?DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_TWO
                        :DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_ONE);
                bpmInstanceApproveEntity.setAssignee_code(k.getCode());
                bpmInstanceApproveEntity.setAssignee_name(k.getName());
                bpmInstanceApproveEntity.setForm_items(bpmInstanceEntity.getForm_items());
                bpmInstanceApproveEntity.setProcess(bpmInstanceEntity.getProcess());
                bpmInstanceApproveEntity.setSerial_id(bpmInstanceEntity.getSerial_id());
                bpmInstanceApproveEntity.setSerial_type(bpmInstanceEntity.getSerial_type());
                bpmInstanceApproveEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
                bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
                bpmInstanceApproveEntity.setIs_next(childNode.getChildren()!=null?childNode.getChildren().getId():null);
                // bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_RUNNING);
                bpmInstanceApproveEntity.setApprove_time(null);
                bpmInstanceApproveEntity.setDue_date(null);
                bpmInstanceApproveEntity.setRemark(null);
                instanceApproveEntities.add(bpmInstanceApproveEntity);
            });
        }

        // 2.保存下一个节点信息
        if (childNode.getChildren() != null && childNode.getChildren().getId()!=null) {
            processNode(orgUserVo,instanceProcessEntities, instanceApproveEntities,childNode.getChildren(), bpmInstanceEntity, null);
        }

        // 3.保存分支节点信息
        if (childNode.getBranchs() != null) {
            for (ChildNode branch : childNode.getBranchs()) {
                processNode(orgUserVo,instanceProcessEntities,instanceApproveEntities,branch,bpmInstanceEntity,null);
            }
        }
    }

    // 封装节点信息
    public void newProcessNode(OrgUserVo orgUserVo, List<BpmInstanceProcessEntity> instanceProcessEntities, List<BpmInstanceApproveEntity> instanceApproveEntities, List<BpmChildNodeVo> bpmChildNodeVos, BpmInstanceEntity bpmInstanceEntity, Task task) {
        for (BpmChildNodeVo bpmChildNodeVo : bpmChildNodeVos) {
            // 结束节点跳出
            if (bpmChildNodeVo.getType().equals("END")){
                continue;
            }

            // 1.获取下一个节点
            String is_next = bpmChildNodeVos.indexOf(bpmChildNodeVo) == bpmChildNodeVos.size()-1?null:bpmChildNodeVos.get(bpmChildNodeVos.indexOf(bpmChildNodeVo)+1).getId();

            // 2.增加第一个节点信息保存任务id
            BpmInstanceProcessEntity bpmInstanceProcessEntity = new BpmInstanceProcessEntity();
            bpmInstanceProcessEntity.setNode_id(bpmChildNodeVo.getId());
            bpmInstanceProcessEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
            bpmInstanceProcessEntity.setIs_next(is_next);
            bpmInstanceProcessEntity.setApproval_mode(bpmChildNodeVo.getApproval_mode());
            bpmInstanceProcessEntity.setNode_type(bpmChildNodeVo.getType());
            bpmInstanceProcessEntity.setName(bpmChildNodeVo.getName());
            bpmInstanceProcessEntity.setOwner_code(orgUserVo.getCode());
            bpmInstanceProcessEntity.setOwner_name(orgUserVo.getName());
            bpmInstanceProcessEntity.setAction(null);
            bpmInstanceProcessEntity.setComment_id(null);
            bpmInstanceProcessEntity.setStart_time(task != null && bpmChildNodeVo.getType().equals("ROOT")?LocalDateTime.now():null);
            bpmInstanceProcessEntity.setFinish_time(null);
            instanceProcessEntities.add(bpmInstanceProcessEntity);

            // 3.生成所有节点用户信息
            if (task != null && bpmChildNodeVo.getType().equals("ROOT")) {
                BpmInstanceApproveEntity bpmInstanceApproveEntity = new BpmInstanceApproveEntity();
                bpmInstanceApproveEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
                bpmInstanceApproveEntity.setTask_id(task.getId());
                bpmInstanceApproveEntity.setType(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_ONE);
                bpmInstanceApproveEntity.setNode_id(bpmChildNodeVo.getId());
                bpmInstanceApproveEntity.setProcess_instance_id(bpmInstanceEntity.getProcess_instance_id());
                bpmInstanceApproveEntity.setProcess_definition_id(bpmInstanceEntity.getProcess_definition_id());
                bpmInstanceApproveEntity.setTask_name(task.getName());
                bpmInstanceApproveEntity.setAssignee_code(task.getAssignee());
                bpmInstanceApproveEntity.setAssignee_name(bpmUsersService.selectByCode(task.getAssignee()).getUser_name());
                bpmInstanceApproveEntity.setForm_items(bpmInstanceEntity.getForm_items());
                bpmInstanceApproveEntity.setProcess(bpmInstanceEntity.getProcess());
                bpmInstanceApproveEntity.setSerial_id(bpmInstanceEntity.getSerial_id());
                bpmInstanceApproveEntity.setSerial_type(bpmInstanceEntity.getSerial_type());
                bpmInstanceApproveEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
                bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
                bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_RUNNING);
                bpmInstanceApproveEntity.setIs_next(is_next);
                bpmInstanceApproveEntity.setRemark(null);
                bpmInstanceApproveEntity.setDue_date(null);
                bpmInstanceApproveEntity.setApprove_time(null);
                instanceApproveEntities.add(bpmInstanceApproveEntity);
            } else if (CollectionUtil.isNotEmpty(bpmChildNodeVo.getUsers())){
                // 生成所有用户操作节点信息
                bpmChildNodeVo.getUsers().forEach(k->{
                    BpmInstanceApproveEntity bpmInstanceApproveEntity = new BpmInstanceApproveEntity();
                    bpmInstanceApproveEntity.setProcess_code(bpmInstanceEntity.getProcess_code());
                    bpmInstanceApproveEntity.setTask_id(null);
                    bpmInstanceApproveEntity.setNode_id(bpmChildNodeVo.getId());
                    bpmInstanceApproveEntity.setProcess_instance_id(bpmInstanceEntity.getProcess_instance_id());
                    bpmInstanceApproveEntity.setProcess_definition_id(bpmInstanceEntity.getProcess_definition_id());
                    bpmInstanceApproveEntity.setTask_name(bpmChildNodeVo.getName());
                    bpmInstanceApproveEntity.setType(bpmChildNodeVo.getType().equals("CC")?DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_TWO
                            :DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_ONE);
                    bpmInstanceApproveEntity.setAssignee_code(k.getCode());
                    bpmInstanceApproveEntity.setAssignee_name(k.getName());
                    bpmInstanceApproveEntity.setForm_items(bpmInstanceEntity.getForm_items());
                    bpmInstanceApproveEntity.setProcess(bpmInstanceEntity.getProcess());
                    bpmInstanceApproveEntity.setSerial_id(bpmInstanceEntity.getSerial_id());
                    bpmInstanceApproveEntity.setSerial_type(bpmInstanceEntity.getSerial_type());
                    bpmInstanceApproveEntity.setStatus(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO);
                    bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ZERO);
                    bpmInstanceApproveEntity.setIs_next(is_next);
                    bpmInstanceApproveEntity.setApprove_time(null);
                    bpmInstanceApproveEntity.setDue_date(null);
                    bpmInstanceApproveEntity.setRemark(null);
                    instanceApproveEntities.add(bpmInstanceApproveEntity);
                });
            }
        }
    }

    /**
     * 获取节点审批人，被flowable uel表达式自动调用
     * @param execution 执行实例
     * @return 该任务的审批人
     */
    @Override
    public List<String> getNodeApprovalUsers(ExecutionEntity execution) {
        //取缓存里面的，判断之前有没有，多实例防止多次解析
        List<String> cacheUsers = taskCache.get(execution.getProcessInstanceId() + execution.getActivityId());
        if (Objects.nonNull(cacheUsers)){
            return cacheUsers;
        }
        log.info("获取节点[{}]的审批人", execution.getActivityId());
        Map propsMap = execution.getVariable(WflowGlobalVarDef.WFLOW_NODE_PROPS, Map.class);
        ApprovalProps props = (ApprovalProps) propsMap.get(execution.getActivityId());
        List<String> approvalUsers = getApprovalUsers(execution, props);
        taskCache.put(execution.getProcessInstanceId() + execution.getActivityId(), approvalUsers);
        return approvalUsers;
    }

    /**
     * 获取审批人
     * @param execution 上下文
     * @param props     节点熟悉
     * @return 审批人ID列表
     */
    public List<String> getApprovalUsers(ExecutionEntity execution, ApprovalProps props) {
        Set<String> userSet = new LinkedHashSet<>();
        switch (props.getAssignedType()) {
            case REFUSE:
                userSet.add(WflowGlobalVarDef.WFLOW_TASK_REFUSE);
                break;
            case SELF: //取流程发起人
                OrgUserVo owner = execution.getVariable("owner", OrgUserVo.class);
                Optional.ofNullable(owner).ifPresent(on -> userSet.add(on.getCode()));
                break;
//            case ROLE: //取角色
//                userSet.addAll(bpmUsersService.selectByCode(props.getRole().stream().map(OrgUserVo::getId).collect(Collectors.toList())));
//                break;
            case FORM_USER: //从表单取
                List<Map<String, Object>> userList = execution.getVariable(props.getFormUser(), List.class);
                Optional.ofNullable(userList).ifPresent(users -> {
                    userSet.addAll(users.stream().map(u -> u.get("code").toString()).collect(Collectors.toList()));
                });
                break;
//            case FORM_DEPT: //从表单取
//                List<Map<String, Object>> deptList = execution.getVariable(props.getFormDept(), List.class);
//                Optional.ofNullable(deptList).ifPresent(users -> {
//                    userSet.addAll(userDeptOrLeaderService.getLeadersByDept(
//                            users.stream().map(u -> u.get("id").toString()).collect(Collectors.toList())));
//                });
//                break;
            case ASSIGN_USER://指定用户
                userSet.addAll(props.getAssignedUser().stream().map(OrgUserVo::getCode).collect(Collectors.toList()));
                break;
//            case ASSIGN_LEADER:
//                List<String> collect = props.getAssignedDept().stream().map(OrgUserVo::getCode).collect(Collectors.toList());
//                userSet.addAll(userDeptOrLeaderService.getLeadersByDept(collect));
//                break;
            case SELF_SELECT: //自选用户，从变量取，这一步在发起流程时设置的
                List<OrgUserVo> selectUsers = execution.getVariable(execution.getActivityId(), List.class);
                Optional.ofNullable(selectUsers).ifPresent(on -> userSet.addAll(on.stream().map(OrgUserVo::getCode).collect(Collectors.toList())));
                break;
//            case LEADER: //用户的指定级别部门主管
//                ProcessInstanceOwnerDto owner2 = execution.getVariable("owner", OrgUserVo.class);
//                String leaderByLevel = userDeptOrLeaderService.getUserLeaderByLevel(owner2.getOwner(),
//                        owner2.getOwnerDeptId(), props.getLeader().getLevel(), props.getLeader().getSkipEmpty());
//                Optional.ofNullable(leaderByLevel).ifPresent(userSet::add);
//                break;
//            case LEADER_TOP: //用户逐级部门主管
//                ProcessInstanceOwnerDto owner3 = execution.getVariable("owner", ProcessInstanceOwnerDto.class);
//                List<String> leaders = userDeptOrLeaderService.getUserLeadersByLevel(owner3.getOwner(),
//                        owner3.getOwnerDeptId(), "TOP".equals(props.getLeaderTop().getEndCondition()) ?
//                                0 : props.getLeaderTop().getEndLevel(), props.getLeaderTop().getSkipEmpty());
//                Optional.ofNullable(leaders).ifPresent(userSet::addAll);
//                break;
        }

        //处理审批人为空时，采取默认策略
        if (CollectionUtil.isEmpty(userSet)) {
            switch (props.getNobody().getHandler()) {
                case TO_USER:
                    userSet.addAll(props.getNobody().getAssignedUser().stream().map(OrgUserVo::getCode).collect(Collectors.toList()));
                    break;
//                case TO_ADMIN: //TODO 注意系统需要包含该角色 WFLOW_APPROVAL_ADMIN
//                    userSet.addAll(userDeptOrLeaderService.getUsersByRoles(CollectionUtil.newArrayList(WflowGlobalVarDef.WFLOW_APPROVAL_ADMIN)));
//                    break;
                case TO_PASS:
                    userSet.add(WflowGlobalVarDef.WFLOW_TASK_AGRRE);
                    break;
                case TO_REFUSE:
                    userSet.add(WflowGlobalVarDef.WFLOW_TASK_REFUSE);
                    break;
            }
        } else {
            //将用户替换为当前代理人
            // return userDeptOrLeaderService.replaceUserAsAgent(userSet);
        }
        return new ArrayList<>(userSet);
    }

}
