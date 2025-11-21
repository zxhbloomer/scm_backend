package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationWorkflowRuntimeMapper;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * AI Chat调用Workflow运行时Service
 *
 * <p>提供AI Chat调用Workflow时的运行时实例管理,包括创建、状态更新、查询等功能</p>
 * <p>完全镜像AiWorkflowRuntimeService,数据保存到ai_conversation_workflow_runtime表</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Slf4j
@Service
public class AiConversationWorkflowRuntimeService extends ServiceImpl<AiConversationWorkflowRuntimeMapper, AiConversationWorkflowRuntimeEntity> {

    @Resource
    private AiConversationWorkflowRuntimeMapper conversationWorkflowRuntimeMapper;

    @Resource
    private AiWorkflowService workflowService;

    @Resource
    private AiConversationWorkflowRuntimeNodeService conversationWorkflowRuntimeNodeService;

    /**
     * 创建工作流运行实例
     *
     * @param userId 用户ID
     * @param workflowId 工作流ID
     * @return 运行时VO
     */
    public AiConversationWorkflowRuntimeVo create(Long userId, Long workflowId) {
        // 获取工作流信息
        AiWorkflowEntity workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: workflowId=" + workflowId);
        }

        AiConversationWorkflowRuntimeEntity runtime = new AiConversationWorkflowRuntimeEntity();
        runtime.setRuntimeUuid(UuidUtil.createShort());
        runtime.setUserId(userId);
        runtime.setWorkflowId(workflowId);

        // 生成对话ID,用于多轮对话上下文管理
        // 格式: tenantCode::workflowUuid::userId (用户+工作流级别记忆)
        String tenantCode = DataSourceHelper.getCurrentDataSourceName();
        String conversationId = tenantCode + "::" + workflow.getWorkflowUuid() + "::" + userId;
        runtime.setConversationId(conversationId);

        runtime.setStatus(1); // 1-运行中

        // 设置创建人和修改人ID（使用传入的userId参数）
        runtime.setC_id(userId);
        runtime.setU_id(userId);

        conversationWorkflowRuntimeMapper.insert(runtime);

        runtime = conversationWorkflowRuntimeMapper.selectById(runtime.getId());

        AiConversationWorkflowRuntimeVo vo = new AiConversationWorkflowRuntimeVo();
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
     * 创建工作流运行实例(使用指定的conversationId)
     *
     * 用于子工作流继承父工作流的对话上下文
     *
     * @param userId 用户ID
     * @param workflowId 工作流ID
     * @param conversationId 继承的conversationId
     * @return 运行时VO
     */
    public AiConversationWorkflowRuntimeVo createWithConversationId(Long userId, Long workflowId, String conversationId) {
        // 获取工作流信息
        AiWorkflowEntity workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: workflowId=" + workflowId);
        }

        AiConversationWorkflowRuntimeEntity runtime = new AiConversationWorkflowRuntimeEntity();
        runtime.setRuntimeUuid(UuidUtil.createShort());
        runtime.setUserId(userId);
        runtime.setWorkflowId(workflowId);

        // 使用传入的conversationId(继承父工作流的对话上下文)
        runtime.setConversationId(conversationId);

        runtime.setStatus(1); // 1-运行中

        // 设置创建人和修改人ID（使用传入的userId参数）
        runtime.setC_id(userId);
        runtime.setU_id(userId);

        conversationWorkflowRuntimeMapper.insert(runtime);

        runtime = conversationWorkflowRuntimeMapper.selectById(runtime.getId());

        AiConversationWorkflowRuntimeVo vo = new AiConversationWorkflowRuntimeVo();
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

        AiConversationWorkflowRuntimeEntity runtime = conversationWorkflowRuntimeMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return;
        }

        // 从WfState的输入数据构建 JSONObject
        JSONObject inputNode = new JSONObject();
        for (NodeIOData data : wfState.getInput()) {
            inputNode.put(data.getName(), data.getContent());
        }

        // 在查出的实体上修改字段(JSON对象转String)
        runtime.setInputData(inputNode.toJSONString());
        runtime.setStatus(1); // 1-运行中

        conversationWorkflowRuntimeMapper.updateById(runtime);
    }

    /**
     * 更新运行实例的输出数据
     *
     * @param id 运行实例ID
     * @param wfState 工作流状态
     * @return 更新后的实体
     */
    public AiConversationWorkflowRuntimeEntity updateOutput(Long id, WfState wfState) {
        AiConversationWorkflowRuntimeEntity runtime = conversationWorkflowRuntimeMapper.selectById(id);
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

        // 在查出的实体上修改字段(JSON对象转String)
        if (!outputNode.isEmpty()) {
            runtime.setOutputData(outputNode.toJSONString());
        }
        if (wfState.getProcessStatus() != null) {
            runtime.setStatus(wfState.getProcessStatus());
        }

        conversationWorkflowRuntimeMapper.updateById(runtime);

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
        AiConversationWorkflowRuntimeEntity runtime = conversationWorkflowRuntimeMapper.selectById(id);
        if (runtime == null) {
            log.error("工作流实例不存在,id:{}", id);
            return;
        }

        // 在查出的实体上修改字段
        runtime.setStatus(status);
        if (StringUtils.isNotBlank(statusRemark)) {
            runtime.setStatusRemark(StringUtils.substring(statusRemark, 0, 500));
        }

        conversationWorkflowRuntimeMapper.updateById(runtime);
    }

    /**
     * 按UUID查询运行实例
     *
     * @param runtimeUuid 运行实例UUID
     * @return 运行实例
     */
    public AiConversationWorkflowRuntimeEntity getByUuid(String runtimeUuid) {
        return conversationWorkflowRuntimeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiConversationWorkflowRuntimeEntity>()
                        .eq(AiConversationWorkflowRuntimeEntity::getRuntimeUuid, runtimeUuid)
                        .last("LIMIT 1")
        );
    }

    /**
     * 根据UUID查询运行实例详情(包含工作流名称等完整信息)
     *
     * @param runtimeUuid 运行实例UUID
     * @return 运行实例VO(包含完整信息)
     */
    public AiConversationWorkflowRuntimeVo getDetailByUuid(String runtimeUuid) {
        // 使用Mapper的VO查询方法，直接获取包含c_name的VO
        AiConversationWorkflowRuntimeVo vo = conversationWorkflowRuntimeMapper.selectVoByRuntimeUuid(runtimeUuid);

        if (vo == null) {
            return null;
        }

        return convertToDetailVo(vo);
    }

    /**
     * 填充VO的扩展字段(工作流名称、执行时长等)
     *
     * @param vo 运行实例VO
     * @return 运行实例VO(包含完整信息)
     */
    private AiConversationWorkflowRuntimeVo convertToDetailVo(AiConversationWorkflowRuntimeVo vo) {
        // 填充工作流名称
        if (vo.getWorkflowId() != null) {
            AiWorkflowEntity workflow = workflowService.getById(vo.getWorkflowId());
            if (workflow != null) {
                vo.setWorkflow_name(workflow.getTitle());
                vo.setWorkflowUuid(workflow.getWorkflowUuid());
            }
        }

        // 计算执行时长(如果有结束时间)
        if (vo.getU_time() != null && vo.getC_time() != null) {
            long duration = java.time.Duration.between(vo.getC_time(), vo.getU_time()).toMillis();
            vo.setElapsed_time(duration);
        }

        // 设置开始时间和结束时间(使用创建时间和更新时间)
        vo.setStart_time(vo.getC_time());
        vo.setEnd_time(vo.getU_time());

        // 填充输入输出数据(确保不为null)
        fillInputOutput(vo);

        return vo;
    }

    /**
     * 物理删除单个运行实例(级联删除)
     *
     * 删除顺序:
     * 1. 删除运行节点记录(ai_conversation_workflow_runtime_node)
     * 2. 删除运行实例主记录(ai_conversation_workflow_runtime)
     *
     * @param runtimeUuid 运行实例UUID
     * @return 是否成功
     */
    public boolean delete(String runtimeUuid) {
        log.info("开始物理删除AI Chat工作流运行记录: {}", runtimeUuid);

        AiConversationWorkflowRuntimeEntity runtime = getByUuid(runtimeUuid);
        if (runtime == null) {
            throw new RuntimeException("运行实例不存在: " + runtimeUuid);
        }

        Long runtimeId = runtime.getId();

        // 1. 级联删除运行节点记录
        int nodeCount = conversationWorkflowRuntimeNodeService.deleteByRuntimeId(runtimeId);
        log.info("删除AI Chat运行节点记录: runtime_id={}, count={}", runtimeId, nodeCount);

        // 2. 物理删除主记录
        int result = conversationWorkflowRuntimeMapper.deleteById(runtimeId);
        log.info("删除AI Chat运行记录: runtime_id={}, result={}", runtimeId, result);

        return result > 0;
    }

    /**
     * 根据对话ID删除所有workflow运行记录(级联删除)
     *
     * 删除顺序:
     * 1. 查询该对话下所有workflow运行实例ID
     * 2. 批量删除运行节点记录(ai_conversation_workflow_runtime_node)
     * 3. 删除运行实例主记录(ai_conversation_workflow_runtime)
     *
     * @param conversationId 对话ID
     * @return 删除的运行实例数量
     */
    public int deleteByConversationId(String conversationId) {
        log.info("开始删除对话关联的workflow运行记录: conversationId={}", conversationId);

        // 1. 查询该对话下的所有运行实例ID列表
        java.util.List<Long> runtimeIds = conversationWorkflowRuntimeMapper.selectIdsByConversationId(conversationId);

        if (runtimeIds.isEmpty()) {
            log.info("对话没有workflow运行记录: conversationId={}", conversationId);
            return 0;
        }

        // 2. 批量删除运行节点记录
        int totalNodeCount = 0;
        for (Long runtimeId : runtimeIds) {
            int nodeCount = conversationWorkflowRuntimeNodeService.deleteByRuntimeId(runtimeId);
            totalNodeCount += nodeCount;
        }
        log.info("删除workflow运行节点记录: conversationId={}, 节点数量={}", conversationId, totalNodeCount);

        // 3. 删除运行实例主记录
        int runtimeCount = conversationWorkflowRuntimeMapper.deleteByConversationId(conversationId);
        log.info("删除workflow运行记录: conversationId={}, 实例数量={}", conversationId, runtimeCount);

        return runtimeCount;
    }

    /**
     * 填充输入输出数据(确保不为null)
     *
     * @param vo 运行时VO
     */
    private void fillInputOutput(AiConversationWorkflowRuntimeVo vo) {
        if (vo.getInputData() == null) {
            vo.setInputData(new JSONObject());
        }
        if (vo.getOutputData() == null) {
            vo.setOutputData(new JSONObject());
        }
    }
}
