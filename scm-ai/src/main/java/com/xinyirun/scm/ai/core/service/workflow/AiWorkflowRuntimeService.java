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

    @Resource
    private AiWorkflowConversationContentService workflowConversationContentService;

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
     * 创建工作流运行实例（使用指定的conversationId）
     *
     * 用于子工作流继承父工作流的对话上下文
     *
     * @param userId 用户ID
     * @param workflowId 工作流ID
     * @param conversationId 继承的conversationId
     * @return 运行时VO
     */
    public AiWorkflowRuntimeVo createWithConversationId(Long userId, Long workflowId, String conversationId) {
        // 获取工作流信息
        AiWorkflowEntity workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: workflowId=" + workflowId);
        }

        AiWorkflowRuntimeEntity runtime = new AiWorkflowRuntimeEntity();
        runtime.setRuntimeUuid(UuidUtil.createShort());
        runtime.setUserId(userId);
        runtime.setWorkflowId(workflowId);

        // 使用传入的conversationId（继承父工作流的对话上下文）
        runtime.setConversationId(conversationId);

        runtime.setStatus(1); // 1-运行中
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
     * 批量删除工作流的所有运行记录（物理删除+级联）
     *
     * 注意：此操作会物理删除所有记录，无法恢复
     *
     * @param workflowUuid 工作流UUID
     * @return 是否成功
     */
    public boolean deleteAll(String workflowUuid) {
        log.info("开始批量物理删除工作流所有运行记录: workflow_uuid={}", workflowUuid);

        AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

        // 查询该工作流的所有运行记录
        List<AiWorkflowRuntimeEntity> runtimeList = aiWorkflowRuntimeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeEntity>()
                        .eq(AiWorkflowRuntimeEntity::getWorkflowId, workflow.getId())
        );

        if (runtimeList.isEmpty()) {
            log.info("工作流没有运行记录, workflow_uuid={}", workflowUuid);
            return true;
        }

        // 逐条调用delete()方法，实现级联删除
        int successCount = 0;
        for (AiWorkflowRuntimeEntity runtime : runtimeList) {
            try {
                boolean result = delete(runtime.getRuntimeUuid());
                if (result) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("删除运行记录失败: runtime_uuid={}", runtime.getRuntimeUuid(), e);
            }
        }

        log.info("批量删除完成: workflow_uuid={}, 总数={}, 成功={}",
                workflowUuid, runtimeList.size(), successCount);

        return successCount > 0;
    }

    /**
     * 物理删除单个运行实例（级联删除）
     *
     * 删除顺序：
     * 1. 删除运行节点记录（ai_workflow_runtime_node）
     * 2. 删除对话历史记录（ai_conversation_content）
     * 3. 删除运行实例主记录（ai_workflow_runtime）
     *
     * @param runtimeUuid 运行实例UUID
     * @return 是否成功
     */
    public boolean delete(String runtimeUuid) {
        log.info("开始物理删除工作流运行记录: {}", runtimeUuid);

        AiWorkflowRuntimeEntity runtime = getByUuid(runtimeUuid);
        if (runtime == null) {
            throw new RuntimeException("运行实例不存在: " + runtimeUuid);
        }

        Long runtimeId = runtime.getId();
        String conversationId = runtime.getConversationId();

        // 1. 级联删除运行节点记录
        int nodeCount = workflowRuntimeNodeService.deleteByRuntimeId(runtimeId);
        log.info("删除运行节点记录: runtime_id={}, count={}", runtimeId, nodeCount);

        // 2. 级联删除对话历史（隐私保护）
        if (StringUtils.isNotBlank(conversationId)) {
            int conversationCount = workflowConversationContentService.deleteByConversationId(conversationId);
            log.info("删除对话历史: conversation_id={}, count={}", conversationId, conversationCount);
        }

        // 3. 物理删除主记录
        int result = aiWorkflowRuntimeMapper.deleteById(runtimeId);
        log.info("删除运行记录: runtime_id={}, result={}", runtimeId, result);

        return result > 0;
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
