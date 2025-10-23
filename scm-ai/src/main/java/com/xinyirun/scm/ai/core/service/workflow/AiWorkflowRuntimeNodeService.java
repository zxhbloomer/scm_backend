package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowRuntimeNodeMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
     * 创建运行时节点记录
     *
     * @param userId 用户ID
     * @param nodeId 节点ID
     * @param wfRuntimeId 运行实例ID
     * @param nodeUuid 节点UUID
     * @param status 初始状态
     * @return 节点执行记录VO
     */
    public AiWorkflowRuntimeNodeVo create(Long userId, Long nodeId, Long wfRuntimeId,
                                           String nodeUuid, Integer status) {
        AiWorkflowRuntimeNodeEntity runtimeNode = new AiWorkflowRuntimeNodeEntity();
        runtimeNode.setRuntimeNodeUuid(UuidUtil.createShort());
        runtimeNode.setWorkflowRuntimeId(wfRuntimeId);
        runtimeNode.setNodeId(nodeId);
        runtimeNode.setStatus(status != null ? status : 1); // 默认1-等待中
        runtimeNode.setUserId(userId);
        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(runtimeNode);

        runtimeNode = baseMapper.selectById(runtimeNode.getId());

        AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(runtimeNode);
        fillInputOutput(vo);
        return vo;
    }

    /**
     * 更新节点输入数据
     *
     * @param id 节点执行记录ID
     * @param inputData 输入数据
     * @param status 执行状态
     * @param statusRemark 状态描述
     */
    public void updateInput(Long id, ObjectNode inputData, Integer status, String statusRemark) {
        if (inputData == null || inputData.isEmpty()) {
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
        updateObj.setInputData(inputData.toString());
        if (status != null) {
            updateObj.setStatus(status);
        }
        if (StringUtils.isNotBlank(statusRemark)) {
            updateObj.setErrorMessage(StringUtils.substring(statusRemark, 0, 500));
        }
        baseMapper.updateById(updateObj);
    }

    /**
     * 更新节点输出数据
     *
     * @param id 节点执行记录ID
     * @param outputData 输出数据
     * @param status 执行状态
     * @param statusRemark 状态描述
     */
    public void updateOutput(Long id, ObjectNode outputData, Integer status, String statusRemark) {
        AiWorkflowRuntimeNodeEntity node = baseMapper.selectById(id);
        if (node == null) {
            log.error("节点实例不存在,id:{}", id);
            return;
        }

        AiWorkflowRuntimeNodeEntity updateObj = new AiWorkflowRuntimeNodeEntity();
        updateObj.setId(id);

        if (outputData != null && !outputData.isEmpty()) {
            updateObj.setOutputData(outputData.toString());
        }

        if (status != null) {
            updateObj.setStatus(status);
        }

        if (StringUtils.isNotBlank(statusRemark)) {
            updateObj.setErrorMessage(StringUtils.substring(statusRemark, 0, 500));
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
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 节点VO
     */
    private void fillInputOutput(AiWorkflowRuntimeNodeVo vo) {
        if (vo.getInput() == null) {
            vo.setInput(JsonNodeFactory.instance.objectNode());
        }
        if (vo.getOutput() == null) {
            vo.setOutput(JsonNodeFactory.instance.objectNode());
        }
    }
}
