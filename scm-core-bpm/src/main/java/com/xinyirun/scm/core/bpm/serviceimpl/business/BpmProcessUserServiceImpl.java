package com.xinyirun.scm.core.bpm.serviceimpl.business;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.core.bpm.mapper.business.BpmProcessTemplatesMapper;
import com.xinyirun.scm.core.bpm.mapper.business.BpmTodoMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BPM审批流人员查询服务实现类
 * 
 * @author xinyirun
 * @since 2025-01-28
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BpmProcessUserServiceImpl implements IBpmProcessUserService {

    @Autowired
    private BpmProcessTemplatesMapper templatesMapper;

    @Autowired
    private BpmTodoMapper todoMapper;

    @Autowired
    private RepositoryService repositoryService;


    /**
     * 获取所有使用中审批流的参与人员
     */
    @Override
    public List<String> getAllProcessUsers() {
        try {
            log.info("开始获取所有审批流参与人员");
            
            // 1. 查询所有使用中的审批流模板
            List<BpmProcessTemplatesEntity> templates = getActiveTemplates();
            log.info("找到{}个使用中的审批流模板", templates.size());

            if (CollectionUtil.isEmpty(templates)) {
                return new ArrayList<>();
            }

            // 2. 提取所有processKey并获取审批流用户
            Set<String> allUserIds = new HashSet<>();
            
            for (BpmProcessTemplatesEntity template : templates) {
                try {
                    String processKey = extractProcessKey(template.getDeployment_id());
                    log.debug("处理审批流模板: {} -> processKey: {}", template.getName(), processKey);
                    
                    Set<String> processUsers = getUsersFromProcess(processKey);
                    allUserIds.addAll(processUsers);
                    
                    log.debug("审批流[{}]包含{}个参与者", processKey, processUsers.size());
                } catch (Exception e) {
                    log.error("解析审批流模板失败: templateId={}, deploymentId={}", 
                        template.getTemplate_id(), template.getDeployment_id(), e);
                    // 继续处理其他模板，不中断整个流程
                }
            }

            log.info("总共找到{}个不同的用户ID", allUserIds.size());

            // 3. 转换为String列表
            return new ArrayList<>(allUserIds);
            
        } catch (Exception e) {
            log.error("获取审批流用户失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据deployment_id解析processKey
     */
    @Override
    public String extractProcessKey(String deploymentId) {
        if (StrUtil.isBlank(deploymentId)) {
            return "";
        }

        try {
            // deployment_id格式: Flowable{template_id}:{version}:{process_definition_id}
            // 示例: FlowableB_SO_ORDER_CANCEL:3:1899007012641820672
            
            // 移除"Flowable"前缀
            if (!deploymentId.startsWith("Flowable")) {
                log.warn("deployment_id格式不正确，不以Flowable开头: {}", deploymentId);
                return deploymentId;
            }
            
            String temp = deploymentId.substring("Flowable".length());
            
            // 提取第一个冒号前的部分作为processKey
            int colonIndex = temp.indexOf(":");
            String processKey = colonIndex > 0 ? temp.substring(0, colonIndex) : temp;
            
            log.debug("解析processKey: {} -> {}", deploymentId, processKey);
            return processKey;
            
        } catch (Exception e) {
            log.error("解析processKey失败: deploymentId={}", deploymentId, e);
            return "";
        }
    }

    /**
     * 获取指定审批流的参与人员
     */
    @Override
    public List<MStaffVo> getProcessUsersByKey(String processKey) {
        try {
            Set<String> userIds = getUsersFromProcess(processKey);
            return convertToStaffVoList(userIds);
        } catch (Exception e) {
            log.error("获取指定审批流用户失败: processKey={}", processKey, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取所有待办任务的处理人员
     */
    @Override
    public List<String> getTodoAssigneeUsers() {
        try {
            log.info("开始获取所有待办任务处理人员");
            
            // 使用SQL直接查询所有待办任务的处理人员编码
            List<String> assigneeCodes = todoMapper.selectTodoAssigneeCodes();
            
            if (CollectionUtil.isEmpty(assigneeCodes)) {
                log.info("未找到待办任务处理人员");
                return new ArrayList<>();
            }
            
            log.info("找到{}个不同的待办任务处理人员", assigneeCodes.size());
            
            // 去重并返回String列表
            Set<String> assigneeCodeSet = new HashSet<>(assigneeCodes);
            return new ArrayList<>(assigneeCodeSet);
            
        } catch (Exception e) {
            log.error("获取待办任务处理人员失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询所有使用中的审批流模板
     */
    private List<BpmProcessTemplatesEntity> getActiveTemplates() {
        LambdaQueryWrapper<BpmProcessTemplatesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BpmProcessTemplatesEntity::getIs_stop, false)
               .isNotNull(BpmProcessTemplatesEntity::getDeployment_id)
               .ne(BpmProcessTemplatesEntity::getDeployment_id, "")
               .orderByDesc(BpmProcessTemplatesEntity::getU_time);
        
        return templatesMapper.selectList(wrapper);
    }

    /**
     * 从流程中获取所有用户ID
     */
    private Set<String> getUsersFromProcess(String processKey) {
        Set<String> users = new HashSet<>();
        
        if (StrUtil.isBlank(processKey)) {
            return users;
        }

        try {
            // 1. 通过Flowable API获取流程定义
            ProcessDefinition def = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
                
            if (def == null) {
                log.warn("未找到流程定义: processKey={}", processKey);
                return users;
            }

            // 2. 解析BPMN模型获取用户任务
            BpmnModel model = repositoryService.getBpmnModel(def.getId());
            
            if (model == null) {
                log.warn("未找到BPMN模型: processKey={}, definitionId={}", processKey, def.getId());
                return users;
            }

            // 3. 遍历所有用户任务节点
            for (Process process : model.getProcesses()) {
                extractUsersFromFlowElements(process.getFlowElements(), users);
            }
            
        } catch (Exception e) {
            log.error("从流程中获取用户失败: processKey={}", processKey, e);
        }
        
        return users;
    }

    /**
     * 从流程元素中提取用户
     */
    private void extractUsersFromFlowElements(Collection<FlowElement> flowElements, Set<String> users) {
        if (CollectionUtil.isEmpty(flowElements)) {
            return;
        }
        
        for (FlowElement element : flowElements) {
            if (element instanceof UserTask) {
                UserTask userTask = (UserTask) element;
                extractUsersFromUserTask(userTask, users);
            } else if (element instanceof SubProcess) {
                // 处理子流程
                SubProcess subProcess = (SubProcess) element;
                extractUsersFromFlowElements(subProcess.getFlowElements(), users);
            }
        }
    }

    /**
     * 从用户任务中提取用户
     */
    private void extractUsersFromUserTask(UserTask userTask, Set<String> users) {
        // 直接指定的用户
        if (StrUtil.isNotBlank(userTask.getAssignee())) {
            String assignee = cleanUserExpression(userTask.getAssignee());
            if (StrUtil.isNotBlank(assignee)) {
                users.add(assignee);
            }
        }

        // 候选用户列表
        if (CollectionUtil.isNotEmpty(userTask.getCandidateUsers())) {
            for (String candidateUser : userTask.getCandidateUsers()) {
                String cleanUser = cleanUserExpression(candidateUser);
                if (StrUtil.isNotBlank(cleanUser)) {
                    users.add(cleanUser);
                }
            }
        }

        // 候选用户组 - 这里先记录组ID，后续需要解析组内用户
        if (CollectionUtil.isNotEmpty(userTask.getCandidateGroups())) {
            for (String groupId : userTask.getCandidateGroups()) {
                String cleanGroupId = cleanUserExpression(groupId);
                if (StrUtil.isNotBlank(cleanGroupId)) {
                    // TODO: 这里需要根据业务逻辑解析用户组内的用户
                    // 暂时将组ID作为用户ID处理，后续可以扩展
                    users.add("GROUP_" + cleanGroupId);
                }
            }
        }
    }

    /**
     * 清理表达式，提取实际的用户名/组名
     */
    private String cleanUserExpression(String expression) {
        if (StrUtil.isBlank(expression)) {
            return "";
        }

        String cleaned = expression.trim();
        
        // 移除Spring EL表达式符号
        if (cleaned.startsWith("${") && cleaned.endsWith("}")) {
            cleaned = cleaned.substring(2, cleaned.length() - 1);
        }

        return cleaned;
    }

    /**
     * 将用户ID集合转换为MStaffVo列表
     */
    private List<MStaffVo> convertToStaffVoList(Set<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }

        List<MStaffVo> staffList = new ArrayList<>();
        
        for (String userId : userIds) {
            try {
                // 创建MStaffVo对象，只包含code
                MStaffVo staff = new MStaffVo();
                staff.setCode(userId);
                staffList.add(staff);
                
            } catch (Exception e) {
                log.error("转换用户信息失败: userId={}", userId, e);
            }
        }

        // 去重并返回
        return staffList.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }
}