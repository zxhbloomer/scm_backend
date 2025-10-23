package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowRuntimeMapper;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI工作流运行时Service
 *
 * <p>提供工作流运行时实例的管理,包括创建、状态更新、查询等功能</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiWorkflowRuntimeService extends ServiceImpl<AiWorkflowRuntimeMapper, AiWorkflowRuntimeEntity> {

    @Resource
    private AiWorkflowService workflowService;

    @Resource
    private AiWorkflowRuntimeNodeService workflowRuntimeNodeService;

    /**
     * 创建工作流运行实例
     *
     * @param userId 用户ID
     * @param workflowId 工作流ID
     * @return 运行时VO
     */
    public AiWorkflowRuntimeVo create(Long userId, Long workflowId) {
        AiWorkflowRuntimeEntity runtime = new AiWorkflowRuntimeEntity();
        runtime.setRuntimeUuid(UuidUtil.createShort());
        runtime.setUserId(userId);
        runtime.setWorkflowId(workflowId);
        runtime.setStatus(1); // 1-运行中
        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(runtime);

        runtime = baseMapper.selectById(runtime.getId());
        return changeRuntimeToDTO(runtime);
    }

    /**
     * 更新运行实例的输入数据
     *
     * @param id 运行实例ID
     * @param wfState 工作流状态
     */
    public void updateInput(Long id, WfState wfState) {
        if (wfState.getInput() == null || wfState.getInput().isEmpty()) {
            log.warn("没有输入数据,id:{}", id);
            return;
        }

        AiWorkflowRuntimeEntity runtime = baseMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return;
        }

        // 从WfState的输入数据构建ObjectNode
        ObjectNode inputNode = com.xinyirun.scm.ai.utils.JsonUtil.createObjectNode();
        for (NodeIOData data : wfState.getInput()) {
            inputNode.set(data.getName(), com.xinyirun.scm.ai.utils.JsonUtil.classToJsonNode(data.getContent()));
        }

        AiWorkflowRuntimeEntity updateObj = new AiWorkflowRuntimeEntity();
        updateObj.setId(id);
        // 直接将ObjectNode转换为Map供数据库存储
        updateObj.setInput(com.xinyirun.scm.ai.utils.JsonUtil.toMap(inputNode));
        updateObj.setStatus(1); // 1-运行中
        baseMapper.updateById(updateObj);
    }

    /**
     * 更新运行实例的输出数据
     *
     * @param id 运行实例ID
     * @param wfState 工作流状态
     * @return 更新后的实体
     */
    public AiWorkflowRuntimeEntity updateOutput(Long id, WfState wfState) {
        AiWorkflowRuntimeEntity runtime = baseMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return null;
        }

        // 从WfState的输出数据构建ObjectNode
        ObjectNode outputNode = com.xinyirun.scm.ai.utils.JsonUtil.createObjectNode();
        if (wfState.getOutput() != null) {
            for (NodeIOData data : wfState.getOutput()) {
                outputNode.set(data.getName(), com.xinyirun.scm.ai.utils.JsonUtil.classToJsonNode(data.getContent()));
            }
        }

        AiWorkflowRuntimeEntity updateObj = new AiWorkflowRuntimeEntity();
        updateObj.setId(id);
        if (!outputNode.isEmpty()) {
            // 直接将ObjectNode转换为Map供数据库存储
            updateObj.setOutput(com.xinyirun.scm.ai.utils.JsonUtil.toMap(outputNode));
        }
        if (wfState.getProcessStatus() != null) {
            updateObj.setStatus(wfState.getProcessStatus());
        }
        baseMapper.updateById(updateObj);

        return updateObj;
    }

    /**
     * 更新运行实例状态
     *
     * @param id 运行实例ID
     * @param status 执行状态
     * @param statusRemark 状态描述
     */
    public void updateStatus(Long id, Integer status, String statusRemark) {
        AiWorkflowRuntimeEntity runtime = baseMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return;
        }

        AiWorkflowRuntimeEntity updateObj = new AiWorkflowRuntimeEntity();
        updateObj.setId(id);
        updateObj.setStatus(status);
        if (StringUtils.isNotBlank(statusRemark)) {
            updateObj.setStatusRemark(StringUtils.substring(statusRemark, 0, 500));
        }
        baseMapper.updateById(updateObj);
    }

    /**
     * 按UUID查询运行实例
     *
     * @param runtimeUuid 运行实例UUID
     * @return 运行实例
     */
    public AiWorkflowRuntimeEntity getByUuid(String runtimeUuid) {
        return baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeEntity>()
                        .eq(AiWorkflowRuntimeEntity::getRuntimeUuid, runtimeUuid)
                        .eq(AiWorkflowRuntimeEntity::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
    }

    /**
     * 分页查询工作流的运行历史
     *
     * @param workflowUuid 工作流UUID
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public Page<AiWorkflowRuntimeVo> page(String workflowUuid, Integer currentPage, Integer pageSize) {
        AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

        Page<AiWorkflowRuntimeEntity> entityPage = baseMapper.selectPage(
                new Page<>(currentPage, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeEntity>()
                        .eq(AiWorkflowRuntimeEntity::getWorkflowId, workflow.getId())
                        .eq(AiWorkflowRuntimeEntity::getIsDeleted, 0)
                        .orderByDesc(AiWorkflowRuntimeEntity::getUTime)
        );

        Page<AiWorkflowRuntimeVo> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());

        List<AiWorkflowRuntimeVo> voList = new ArrayList<>();
        for (AiWorkflowRuntimeEntity entity : entityPage.getRecords()) {
            AiWorkflowRuntimeVo vo = changeRuntimeToDTO(entity);
            fillInputOutput(vo);
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 查询运行实例的节点执行记录
     *
     * @param runtimeUuid 运行实例UUID
     * @return 节点执行记录列表
     */
    public List<AiWorkflowRuntimeNodeVo> listByRuntimeUuid(String runtimeUuid) {
        AiWorkflowRuntimeEntity runtime = getByUuid(runtimeUuid);
        if (runtime == null) {
            throw new RuntimeException("运行实例不存在: " + runtimeUuid);
        }
        return workflowRuntimeNodeService.listByWfRuntimeId(runtime.getId());
    }

    /**
     * 删除工作流的所有运行记录
     *
     * @param workflowUuid 工作流UUID
     * @return 是否成功
     */
    public boolean deleteAll(String workflowUuid) {
        AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

        return baseMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<AiWorkflowRuntimeEntity>()
                        .eq(AiWorkflowRuntimeEntity::getWorkflowId, workflow.getId())
                        .set(AiWorkflowRuntimeEntity::getIsDeleted, 1)
        ) > 0;
    }

    /**
     * 软删除单个运行实例
     *
     * @param runtimeUuid 运行实例UUID
     * @return 是否成功
     */
    public boolean softDelete(String runtimeUuid) {
        AiWorkflowRuntimeEntity runtime = getByUuid(runtimeUuid);
        if (runtime == null) {
            throw new RuntimeException("运行实例不存在: " + runtimeUuid);
        }

        AiWorkflowRuntimeEntity updateObj = new AiWorkflowRuntimeEntity();
        updateObj.setId(runtime.getId());
        updateObj.setIsDeleted(1);
        return baseMapper.updateById(updateObj) > 0;
    }

    /**
     * 将运行实例实体转换为VO
     *
     * @param runtime 运行实例实体
     * @return 运行实例VO
     */
    private AiWorkflowRuntimeVo changeRuntimeToDTO(AiWorkflowRuntimeEntity runtime) {
        AiWorkflowRuntimeVo vo = new AiWorkflowRuntimeVo();
        BeanUtils.copyProperties(runtime, vo);
        return vo;
    }

    /**
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 运行实例VO
     */
    private void fillInputOutput(AiWorkflowRuntimeVo vo) {
        if (vo.getInput() == null) {
            vo.setInput(com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode());
        }
        if (vo.getOutput() == null) {
            vo.setOutput(com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode());
        }
    }
}
