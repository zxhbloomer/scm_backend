package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationRuntimeNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationRuntimeNodeMapper;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Chat调用Workflow运行时节点Service
 *
 * <p>提供AI Chat调用Workflow时的节点管理,包括节点状态跟踪、输入输出记录等功能</p>
 * <p>完全镜像AiWorkflowRuntimeNodeService,数据保存到ai_conversation_runtime_node表</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Slf4j
@Service
public class AiConversationRuntimeNodeService extends ServiceImpl<AiConversationRuntimeNodeMapper, AiConversationRuntimeNodeEntity> {

    @Resource
    private AiConversationRuntimeNodeMapper conversationRuntimeNodeMapper;

    @Resource
    private AiWorkflowNodeService workflowNodeService;

    @Resource
    private com.xinyirun.scm.ai.core.service.chat.AiTokenUsageService aiTokenUsageService;

    /**
     * 查询运行实例的所有节点执行记录
     *
     * @param wfRuntimeId 运行实例ID
     * @return 节点执行记录VO列表
     */
    public List<AiConversationRuntimeNodeVo> listByWfRuntimeId(Long wfRuntimeId) {
        List<AiConversationRuntimeNodeEntity> entityList = conversationRuntimeNodeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiConversationRuntimeNodeEntity>()
                        .eq(AiConversationRuntimeNodeEntity::getConversationWorkflowRuntimeId, wfRuntimeId)
                        .orderByAsc(AiConversationRuntimeNodeEntity::getId)
        );

        List<AiConversationRuntimeNodeVo> result = new ArrayList<>();
        for (AiConversationRuntimeNodeEntity entity : entityList) {
            AiConversationRuntimeNodeVo vo = new AiConversationRuntimeNodeVo();
            BeanUtils.copyProperties(entity, vo);

            // 手动转换 JSON 字段: String → JSONObject
            if (StringUtils.isNotBlank(entity.getInputData())) {
                vo.setInputData(JSON.parseObject(entity.getInputData()));
            }
            if (StringUtils.isNotBlank(entity.getOutputData())) {
                vo.setOutputData(JSON.parseObject(entity.getOutputData()));
            }

            // ⭐ 填充节点标题：通过nodeId查询ai_workflow_node表获取title
            // 前端执行详情页面直接使用nodeTitle字段显示节点名称，避免通过nodeId匹配workflow.nodes
            if (entity.getNodeId() != null) {
                var node = workflowNodeService.getById(entity.getNodeId());
                if (node != null && StringUtils.isNotBlank(node.getTitle())) {
                    vo.setNodeTitle(node.getTitle());
                }
            }

            // 📊 填充Token消耗信息：从ai_token_usage查询该节点的Token统计
            if (entity.getId() != null) {
                com.xinyirun.scm.ai.bean.vo.chat.NodeTokenUsageVo tokenUsage =
                        aiTokenUsageService.getNodeTokenUsage(entity.getId());
                if (tokenUsage != null) {
                    vo.setPromptTokens(tokenUsage.getPromptTokens());
                    vo.setCompletionTokens(tokenUsage.getCompletionTokens());
                    vo.setTotalTokens(tokenUsage.getTotalTokens());
                    log.debug("📊【Token统计】节点Token填充 - nodeId={}, totalTokens={}",
                            entity.getId(), tokenUsage.getTotalTokens());
                }
            }

            fillInputOutput(vo);
            result.add(vo);
        }

        return result;
    }

    /**
     * 根据节点状态创建运行时节点记录
     *
     * @param wfRuntimeId 运行实例ID
     * @param state 节点状态
     * @param wfNodeId 节点ID
     * @param userId 用户ID，用于设置创建人和修改人
     * @return 节点执行记录VO
     */
    public AiConversationRuntimeNodeVo createByState(Long wfRuntimeId, WfNodeState state, Long wfNodeId, Long userId) {
        AiConversationRuntimeNodeEntity runtimeNode = new AiConversationRuntimeNodeEntity();
        runtimeNode.setRuntimeNodeUuid(state.getUuid());
        runtimeNode.setConversationWorkflowRuntimeId(wfRuntimeId);
        runtimeNode.setNodeId(wfNodeId);
        runtimeNode.setStatus(state.getProcessStatus());

        // 设置创建人和修改人ID（使用传入的userId参数）
        runtimeNode.setC_id(userId);
        runtimeNode.setU_id(userId);

        conversationRuntimeNodeMapper.insert(runtimeNode);

        // 重新查询获取完整数据
        runtimeNode = conversationRuntimeNodeMapper.selectById(runtimeNode.getId());

        // 转换为 VO
        AiConversationRuntimeNodeVo vo = new AiConversationRuntimeNodeVo();
        BeanUtils.copyProperties(runtimeNode, vo);

        // 手动转换 JSON 字段: String → JSONObject
        if (StringUtils.isNotBlank(runtimeNode.getInputData())) {
            vo.setInputData(JSON.parseObject(runtimeNode.getInputData()));
        }
        if (StringUtils.isNotBlank(runtimeNode.getOutputData())) {
            vo.setOutputData(JSON.parseObject(runtimeNode.getOutputData()));
        }

        fillInputOutput(vo);
        return vo;
    }

    /**
     * 更新节点输入数据
     *
     * @param id 节点执行记录ID
     * @param state 工作流节点状态
     */
    public void updateInput(Long id, WfNodeState state) {
        if (CollectionUtils.isEmpty(state.getInputs())) {
            log.warn("没有输入数据,id:{}", id);
            return;
        }

        AiConversationRuntimeNodeEntity node = conversationRuntimeNodeMapper.selectById(id);
        if (node == null) {
            log.error("节点实例不存在,id:{}", id);
            return;
        }

        // 在查询出的实体上修改字段
        JSONObject inputNode = new JSONObject();
        for (NodeIOData data : state.getInputs()) {
            inputNode.put(data.getName(), data.getContent());
        }
        node.setInputData(inputNode.toJSONString());

        if (state.getProcessStatus() != null) {
            node.setStatus(state.getProcessStatus());
        }
        if (StringUtils.isNotBlank(state.getProcessStatusRemark())) {
            node.setStatusRemark(StringUtils.substring(state.getProcessStatusRemark(), 0, 500));
        }

        conversationRuntimeNodeMapper.updateById(node);
    }

    /**
     * 更新节点输出数据
     *
     * @param id 节点执行记录ID
     * @param state 工作流节点状态
     */
    public void updateOutput(Long id, WfNodeState state) {
        AiConversationRuntimeNodeEntity node = conversationRuntimeNodeMapper.selectById(id);
        if (node == null) {
            log.error("节点实例不存在,id:{}", id);
            return;
        }

        // 在查询出的实体上修改字段
        if (!CollectionUtils.isEmpty(state.getOutputs())) {
            JSONObject outputNode = new JSONObject();
            for (NodeIOData data : state.getOutputs()) {
                outputNode.put(data.getName(), data.getContent());
            }
            node.setOutputData(outputNode.toJSONString());
        }

        if (state.getProcessStatus() != null) {
            node.setStatus(state.getProcessStatus());
        }

        if (StringUtils.isNotBlank(state.getProcessStatusRemark())) {
            node.setStatusRemark(StringUtils.substring(state.getProcessStatusRemark(), 0, 500));
        }

        conversationRuntimeNodeMapper.updateById(node);
    }

    /**
     * 删除运行时节点记录
     *
     * @param id 节点执行记录ID
     */
    public void delete(Long id) {
        conversationRuntimeNodeMapper.deleteById(id);
    }

    /**
     * 根据运行实例ID批量删除节点记录
     *
     * @param runtimeId 运行实例ID
     * @return 删除的记录数
     */
    public int deleteByRuntimeId(Long runtimeId) {
        int count = conversationRuntimeNodeMapper.deleteByRuntimeId(runtimeId);
        log.info("根据runtime_id批量删除AI Chat节点记录, runtime_id: {}, 删除数量: {}", runtimeId, count);
        return count;
    }

    /**
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 节点VO
     */
    private void fillInputOutput(AiConversationRuntimeNodeVo vo) {
        if (vo.getInputData() == null) {
            vo.setInputData(new JSONObject());
        }
        if (vo.getOutputData() == null) {
            vo.setOutputData(new JSONObject());
        }
    }

    /**
     * 获取第一个节点的更新时间(工作流开始时间)
     *
     * @param wfRuntimeId 运行实例ID
     * @return 第一个节点的u_time,如果无节点返回null
     */
    public java.time.LocalDateTime getFirstNodeEndTime(Long wfRuntimeId) {
        AiConversationRuntimeNodeEntity firstNode = conversationRuntimeNodeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiConversationRuntimeNodeEntity>()
                        .eq(AiConversationRuntimeNodeEntity::getConversationWorkflowRuntimeId, wfRuntimeId)
                        .orderByAsc(AiConversationRuntimeNodeEntity::getId)
                        .last("LIMIT 1")
        );
        return firstNode != null ? firstNode.getU_time() : null;
    }

    /**
     * 获取最后一个节点的更新时间(工作流结束时间)
     *
     * @param wfRuntimeId 运行实例ID
     * @return 最后一个节点的u_time,如果无节点返回null
     */
    public java.time.LocalDateTime getLastNodeEndTime(Long wfRuntimeId) {
        AiConversationRuntimeNodeEntity lastNode = conversationRuntimeNodeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiConversationRuntimeNodeEntity>()
                        .eq(AiConversationRuntimeNodeEntity::getConversationWorkflowRuntimeId, wfRuntimeId)
                        .orderByDesc(AiConversationRuntimeNodeEntity::getId)
                        .last("LIMIT 1")
        );
        return lastNode != null ? lastNode.getU_time() : null;
    }
}
