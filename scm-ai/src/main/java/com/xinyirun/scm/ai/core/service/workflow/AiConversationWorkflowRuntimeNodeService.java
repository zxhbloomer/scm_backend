package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationWorkflowRuntimeNodeMapper;
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
 * <p>完全镜像AiWorkflowRuntimeNodeService,数据保存到ai_conversation_workflow_runtime_node表</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Slf4j
@Service
public class AiConversationWorkflowRuntimeNodeService extends ServiceImpl<AiConversationWorkflowRuntimeNodeMapper, AiConversationWorkflowRuntimeNodeEntity> {

    @Resource
    private AiConversationWorkflowRuntimeNodeMapper conversationWorkflowRuntimeNodeMapper;

    @Resource
    private AiWorkflowNodeService workflowNodeService;

    /**
     * 查询运行实例的所有节点执行记录
     *
     * @param wfRuntimeId 运行实例ID
     * @return 节点执行记录VO列表
     */
    public List<AiConversationWorkflowRuntimeNodeVo> listByWfRuntimeId(Long wfRuntimeId) {
        List<AiConversationWorkflowRuntimeNodeEntity> entityList = conversationWorkflowRuntimeNodeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiConversationWorkflowRuntimeNodeEntity>()
                        .eq(AiConversationWorkflowRuntimeNodeEntity::getConversationWorkflowRuntimeId, wfRuntimeId)
                        .orderByAsc(AiConversationWorkflowRuntimeNodeEntity::getId)
        );

        List<AiConversationWorkflowRuntimeNodeVo> result = new ArrayList<>();
        for (AiConversationWorkflowRuntimeNodeEntity entity : entityList) {
            AiConversationWorkflowRuntimeNodeVo vo = new AiConversationWorkflowRuntimeNodeVo();
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
    public AiConversationWorkflowRuntimeNodeVo createByState(Long wfRuntimeId, WfNodeState state, Long wfNodeId, Long userId) {
        AiConversationWorkflowRuntimeNodeEntity runtimeNode = new AiConversationWorkflowRuntimeNodeEntity();
        runtimeNode.setRuntimeNodeUuid(state.getUuid());
        runtimeNode.setConversationWorkflowRuntimeId(wfRuntimeId);
        runtimeNode.setNodeId(wfNodeId);
        runtimeNode.setStatus(state.getProcessStatus());

        // 设置创建人和修改人ID（使用传入的userId参数）
        runtimeNode.setC_id(userId);
        runtimeNode.setU_id(userId);

        conversationWorkflowRuntimeNodeMapper.insert(runtimeNode);

        // 重新查询获取完整数据
        runtimeNode = conversationWorkflowRuntimeNodeMapper.selectById(runtimeNode.getId());

        // 转换为 VO
        AiConversationWorkflowRuntimeNodeVo vo = new AiConversationWorkflowRuntimeNodeVo();
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

        AiConversationWorkflowRuntimeNodeEntity node = conversationWorkflowRuntimeNodeMapper.selectById(id);
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

        conversationWorkflowRuntimeNodeMapper.updateById(node);
    }

    /**
     * 更新节点输出数据
     *
     * @param id 节点执行记录ID
     * @param state 工作流节点状态
     */
    public void updateOutput(Long id, WfNodeState state) {
        AiConversationWorkflowRuntimeNodeEntity node = conversationWorkflowRuntimeNodeMapper.selectById(id);
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

        conversationWorkflowRuntimeNodeMapper.updateById(node);
    }

    /**
     * 删除运行时节点记录
     *
     * @param id 节点执行记录ID
     */
    public void delete(Long id) {
        conversationWorkflowRuntimeNodeMapper.deleteById(id);
    }

    /**
     * 根据运行实例ID批量删除节点记录
     *
     * @param runtimeId 运行实例ID
     * @return 删除的记录数
     */
    public int deleteByRuntimeId(Long runtimeId) {
        int count = conversationWorkflowRuntimeNodeMapper.deleteByRuntimeId(runtimeId);
        log.info("根据runtime_id批量删除AI Chat节点记录, runtime_id: {}, 删除数量: {}", runtimeId, count);
        return count;
    }

    /**
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 节点VO
     */
    private void fillInputOutput(AiConversationWorkflowRuntimeNodeVo vo) {
        if (vo.getInputData() == null) {
            vo.setInputData(new JSONObject());
        }
        if (vo.getOutputData() == null) {
            vo.setOutputData(new JSONObject());
        }
    }
}
