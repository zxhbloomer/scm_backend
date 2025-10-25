package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowRuntimeNodeMapper;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AI工作流运行时节点Service
 *
 * <p>提供工作流运行时节点的管理,包括节点状态跟踪、输入输出记录等功能</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiWorkflowRuntimeNodeService extends ServiceImpl<AiWorkflowRuntimeNodeMapper, AiWorkflowRuntimeNodeEntity> {

    /**
     * 查询运行实例的所有节点执行记录
     *
     * @param wfRuntimeId 运行实例ID
     * @return 节点执行记录VO列表
     */
    public List<AiWorkflowRuntimeNodeVo> listByWfRuntimeId(Long wfRuntimeId) {
        List<AiWorkflowRuntimeNodeEntity> entityList = baseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeNodeEntity>()
                        .eq(AiWorkflowRuntimeNodeEntity::getWorkflowRuntimeId, wfRuntimeId)
                        .eq(AiWorkflowRuntimeNodeEntity::getIsDeleted, 0)
                        .orderByAsc(AiWorkflowRuntimeNodeEntity::getId)
        );

        List<AiWorkflowRuntimeNodeVo> result = new ArrayList<>();
        for (AiWorkflowRuntimeNodeEntity entity : entityList) {
            AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(entity);
            fillInputOutput(vo);
            result.add(vo);
        }

        return result;
    }

    /**
     * 根据节点状态创建运行时节点记录
     * 参考 aideepin: WorkflowRuntimeNodeService.createByState() 第45-59行
     *
     * @param userId 用户ID
     * @param wfNodeId 节点ID
     * @param wfRuntimeId 运行实例ID
     * @param state 节点状态
     * @return 节点执行记录VO
     */
    public AiWorkflowRuntimeNodeVo createByState(Long userId, Long wfNodeId,
                                                  Long wfRuntimeId, WfNodeState state) {
        // 参考 aideepin:46-52
        AiWorkflowRuntimeNodeEntity runtimeNode = new AiWorkflowRuntimeNodeEntity();
        runtimeNode.setRuntimeNodeUuid(state.getUuid());
        runtimeNode.setWorkflowRuntimeId(wfRuntimeId);
        runtimeNode.setNodeId(wfNodeId);
        runtimeNode.setStatus(state.getProcessStatus());
        runtimeNode.setIsDeleted(false);
        // 不设置 c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(runtimeNode);

        // 参考 aideepin:53 - 重新查询获取完整数据
        runtimeNode = baseMapper.selectById(runtimeNode.getId());

        // 参考 aideepin:55-58 - 转换为 VO
        AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(runtimeNode);
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

        AiWorkflowRuntimeNodeEntity node = baseMapper.selectById(id);
        if (node == null) {
            log.error("节点实例不存在,id:{}", id);
            return;
        }

        AiWorkflowRuntimeNodeEntity updateObj = new AiWorkflowRuntimeNodeEntity();
        updateObj.setId(id);

        JSONObject inputNode = new JSONObject();
        for (NodeIOData data : state.getInputs()) {
            inputNode.put(data.getName(), data.getContent());
        }
        updateObj.setInput(inputNode);

        if (state.getProcessStatus() != null) {
            updateObj.setStatus(state.getProcessStatus());
        }
        if (StringUtils.isNotBlank(state.getProcessStatusRemark())) {
            updateObj.setStatusRemark(StringUtils.substring(state.getProcessStatusRemark(), 0, 500));
        }
        baseMapper.updateById(updateObj);
    }

    /**
     * 更新节点输出数据
     *
     * @param id 节点执行记录ID
     * @param state 工作流节点状态
     */
    public void updateOutput(Long id, WfNodeState state) {
        AiWorkflowRuntimeNodeEntity node = baseMapper.selectById(id);
        if (node == null) {
            log.error("节点实例不存在,id:{}", id);
            return;
        }

        AiWorkflowRuntimeNodeEntity updateObj = new AiWorkflowRuntimeNodeEntity();
        updateObj.setId(id);

        if (!CollectionUtils.isEmpty(state.getOutputs())) {
            JSONObject outputNode = new JSONObject();
            for (NodeIOData data : state.getOutputs()) {
                outputNode.put(data.getName(), data.getContent());
            }
            updateObj.setOutput(outputNode);
        }

        if (state.getProcessStatus() != null) {
            updateObj.setStatus(state.getProcessStatus());
        }

        if (StringUtils.isNotBlank(state.getProcessStatusRemark())) {
            updateObj.setStatusRemark(StringUtils.substring(state.getProcessStatusRemark(), 0, 500));
        }

        baseMapper.updateById(updateObj);
    }

    /**
     * 将节点实体转换为VO
     *
     * @param entity 节点实体
     * @return 节点VO
     */
    private AiWorkflowRuntimeNodeVo changeNodeToDTO(AiWorkflowRuntimeNodeEntity entity) {
        AiWorkflowRuntimeNodeVo vo = new AiWorkflowRuntimeNodeVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 删除运行时节点记录
     *
     * @param id 节点执行记录ID
     */
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    /**
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 节点VO
     */
    private void fillInputOutput(AiWorkflowRuntimeNodeVo vo) {
        if (vo.getInput() == null) {
            vo.setInput(new JSONObject());
        }
        if (vo.getOutput() == null) {
            vo.setOutput(new JSONObject());
        }
    }
}
