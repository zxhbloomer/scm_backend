package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowRuntimeMapper;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
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
    private AiWorkflowRuntimeMapper aiWorkflowRuntimeMapper;

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
        // 获取工作流信息
        AiWorkflowEntity workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: workflowId=" + workflowId);
        }

        AiWorkflowRuntimeEntity runtime = new AiWorkflowRuntimeEntity();
        runtime.setRuntimeUuid(UuidUtil.createShort());
        runtime.setUserId(userId);
        runtime.setWorkflowId(workflowId);

        // 生成对话ID,用于多轮对话上下文管理
        // 格式: tenantCode::workflowUuid::userId (用户+工作流级别记忆)
        String tenantCode = DataSourceHelper.getCurrentDataSourceName();
        String conversationId = tenantCode + "::" + workflow.getWorkflowUuid() + "::" + userId;
        runtime.setConversationId(conversationId);

        runtime.setStatus(1); // 1-运行中
        runtime.setIsDeleted(false);
        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        aiWorkflowRuntimeMapper.insert(runtime);

        runtime = aiWorkflowRuntimeMapper.selectById(runtime.getId());

        AiWorkflowRuntimeVo vo = new AiWorkflowRuntimeVo();
        BeanUtils.copyProperties(runtime, vo);

        // 手动转换 JSON 字段: String → JSONObject
        if (StringUtils.isNotBlank(runtime.getInputData())) {
            vo.setInputData(JSON.parseObject(runtime.getInputData()));
        }
        if (StringUtils.isNotBlank(runtime.getOutputData())) {
            vo.setOutputData(JSON.parseObject(runtime.getOutputData()));
        }

        return vo;
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

        AiWorkflowRuntimeEntity runtime = aiWorkflowRuntimeMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return;
        }

        // 从WfState的输入数据构建 JSONObject
        JSONObject inputNode = new JSONObject();
        for (NodeIOData data : wfState.getInput()) {
            inputNode.put(data.getName(), data.getContent());
        }

        // 在查出的实体上修改字段（JSON对象转String）
        runtime.setInputData(inputNode.toJSONString());
        runtime.setStatus(1); // 1-运行中

        aiWorkflowRuntimeMapper.updateById(runtime);
    }

    /**
     * 更新运行实例的输出数据
     *
     * @param id 运行实例ID
     * @param wfState 工作流状态
     * @return 更新后的实体
     */
    public AiWorkflowRuntimeEntity updateOutput(Long id, WfState wfState) {
        AiWorkflowRuntimeEntity runtime = aiWorkflowRuntimeMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return null;
        }

        // 从WfState的输出数据构建 JSONObject
        JSONObject outputNode = new JSONObject();
        if (wfState.getOutput() != null) {
            for (NodeIOData data : wfState.getOutput()) {
                outputNode.put(data.getName(), data.getContent());
            }
        }

        // 在查出的实体上修改字段（JSON对象转String）
        if (!outputNode.isEmpty()) {
            runtime.setOutputData(outputNode.toJSONString());
        }
        if (wfState.getProcessStatus() != null) {
            runtime.setStatus(wfState.getProcessStatus());
        }

        aiWorkflowRuntimeMapper.updateById(runtime);

        return runtime;
    }

    /**
     * 更新运行实例状态
     *
     * @param id 运行实例ID
     * @param status 执行状态
     * @param statusRemark 状态描述
     */
    public void updateStatus(Long id, Integer status, String statusRemark) {
        AiWorkflowRuntimeEntity runtime = aiWorkflowRuntimeMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return;
        }

        // 在查出的实体上修改字段
        runtime.setStatus(status);
        if (StringUtils.isNotBlank(statusRemark)) {
            runtime.setStatusRemark(StringUtils.substring(statusRemark, 0, 500));
        }

        aiWorkflowRuntimeMapper.updateById(runtime);
    }

    /**
     * 按UUID查询运行实例
     *
     * @param runtimeUuid 运行实例UUID
     * @return 运行实例
     */
    public AiWorkflowRuntimeEntity getByUuid(String runtimeUuid) {
        return aiWorkflowRuntimeMapper.selectOne(
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

        Page<AiWorkflowRuntimeEntity> entityPage = aiWorkflowRuntimeMapper.selectPage(
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
            AiWorkflowRuntimeVo vo = new AiWorkflowRuntimeVo();
            BeanUtils.copyProperties(entity, vo);

            // 手动转换 JSON 字段: String → JSONObject
            if (StringUtils.isNotBlank(entity.getInputData())) {
                vo.setInputData(JSON.parseObject(entity.getInputData()));
            }
            if (StringUtils.isNotBlank(entity.getOutputData())) {
                vo.setOutputData(JSON.parseObject(entity.getOutputData()));
            }

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

        return aiWorkflowRuntimeMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<AiWorkflowRuntimeEntity>()
                        .eq(AiWorkflowRuntimeEntity::getWorkflowId, workflow.getId())
                        .set(AiWorkflowRuntimeEntity::getIsDeleted, 1)
        ) > 0;
    }

    /**
     * 软删除单个运行实例
     * 符合SCM标准: 先selectById查询完整实体,然后set修改字段,最后updateById更新
     *
     * @param runtimeUuid 运行实例UUID
     * @return 是否成功
     */
    public boolean softDelete(String runtimeUuid) {
        // 1. 先查询出完整实体
        AiWorkflowRuntimeEntity runtime = getByUuid(runtimeUuid);
        if (runtime == null) {
            throw new RuntimeException("运行实例不存在: " + runtimeUuid);
        }

        // 2. 在查询出的对象上直接修改字段
        runtime.setIsDeleted(true);

        // 3. 使用完整对象更新(其他字段不受影响)
        return aiWorkflowRuntimeMapper.updateById(runtime) > 0;
    }


    /**
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 运行实例VO
     */
    private void fillInputOutput(AiWorkflowRuntimeVo vo) {
        if (vo.getInputData() == null) {
            vo.setInputData(new JSONObject());
        }
        if (vo.getOutputData() == null) {
            vo.setOutputData(new JSONObject());
        }
    }
}
