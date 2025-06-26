package com.xinyirun.scm.core.whapp.serviceimpl.bpm;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.bpm.enums.NodeTypeEnum;
import com.xinyirun.scm.bean.bpm.vo.ProcessNode;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.bean.whapp.vo.business.bpm.WhAppBBpmProcessVo;
import com.xinyirun.scm.bean.whapp.vo.business.bpm.WhAppBpmProcessJson;
import com.xinyirun.scm.bean.whapp.vo.master.user.WhAppStaffUserBpmInfoVo;
import com.xinyirun.scm.common.bpm.WorkFlowConstants;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.whapp.mapper.bpm.WhAppBpmProcessTemplatesMapper;
import com.xinyirun.scm.core.whapp.service.bpm.WhAppIBpmProcessTemplatesService;
import com.xinyirun.scm.core.whapp.service.master.user.WhAppIMStaffService;
import com.xinyirun.scm.core.whapp.service.master.user.WhAppIMUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * process_templates 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@Slf4j
@Service
public class WhAppBpmProcessTemplatesServiceImpl extends ServiceImpl<WhAppBpmProcessTemplatesMapper, BpmProcessTemplatesEntity> implements WhAppIBpmProcessTemplatesService {

    //超时缓存，数据缓存20秒，用来存储审批人防止flowable高频调用
    private static final TimedCache<String, List<String>> taskCache = CacheUtil.newTimedCache(20000);

    @Autowired
    private WhAppBpmProcessTemplatesMapper mapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WhAppIMStaffService whAppIMStaffService;

    @Autowired
    private WhAppIMUserService whAppIMUserService;

    /**
     * 审批流程数据
     *
     * @param param 包含审批流程参数的对象
     * @return 包含审批流程数据的对象
     */
    @Override
    public WhAppBBpmProcessVo generateEngineFlow(WhAppBBpmProcessVo param) {
        String code = null;

        Long _userId = SecurityUtil.getStaff_id();
        String userId = _userId.toString();
        MStaffVo mStaffVo = whAppIMStaffService.selectByid(_userId);

        String userCode = mStaffVo.getCode();
        String userName = mStaffVo.getName();
        String userAvatar = whAppIMUserService.selectUserById(mStaffVo.getUser_id()).getAvatar();

        // 1.根据code获取流程模板
        switch (param.getSerial_type()){
            // 出库计划
            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN:
                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_B_OUT_PLAN;
                break;
            // 企业管理
            case SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_CUSTOMER:
                code = SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_M_CUSTOMER;
                break;
            default:
               return null;
        }

        // 2.根据code获取流程模板
        WhAppBBpmProcessVo WhAppBBpmProcessVo = mapper.selByCode(code);
        if (WhAppBBpmProcessVo==null){
            return null;
        }

        // 3.判断当前登录用户是否在发起审批流名单内
        OrgUserVo userInfo = new OrgUserVo();
        List<OrgUserVo> orgUserVoList = JSONArray.parseArray(WhAppBBpmProcessVo.getOrgUserVoList().toString(), OrgUserVo.class);
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
        WhAppBBpmProcessVo.setOrgUserVo(userInfo);

        // 审批流程定义id
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(WorkFlowConstants.PROCESS_PREFIX + WhAppBBpmProcessVo.getTemplate_id()).latestVersion().singleResult();
        WhAppBBpmProcessVo.setProcess_definition_id(processDefinition == null ? null : processDefinition.getId());

        return WhAppBBpmProcessVo;
    }

    /**
     * 获取审批流程模型数据
     * @param param
     * @return
     */
    public WhAppBpmProcessJson getProcessModel(WhAppBBpmProcessVo param) {
        String process = generateEngineFlow(param).getProcess();
        ProcessNode<?> root = reloadProcessByStr(process).get("root");
        WhAppBpmProcessJson rtn = new WhAppBpmProcessJson();
        rtn.setRoot(root);
        WhAppStaffUserBpmInfoVo _param = new WhAppStaffUserBpmInfoVo();
        _param.setId(SecurityUtil.getStaff_id());
        rtn.setAppStaffVo(whAppIMStaffService.getBpmDataByStaffid(_param));
        return rtn;
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
}
