package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowEdgeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowMapper;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * AI工作流Service
 *
 * <p>提供工作流的创建、复制、更新、删除、查询等功能</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiWorkflowService extends ServiceImpl<AiWorkflowMapper, AiWorkflowEntity> {

    @Resource
    private AiWorkflowMapper aiWorkflowMapper;

    @Lazy
    @Resource
    private AiWorkflowService self;

    @Resource
    private AiWorkflowNodeService workflowNodeService;

    @Resource
    private AiWorkflowEdgeService workflowEdgeService;

    @Resource
    private AiWorkflowComponentService workflowComponentService;

    /**
     * 创建工作流
     *
     * @param vo 工作流VO对象(包含title, remark, isPublic及可选的desc, keywords, priority)
     * @return 工作流VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowVo add(AiWorkflowVo vo) {
        Long userId = SecurityUtil.getStaff_id();
        String workflowUuid = UuidUtil.createShort();

        AiWorkflowEntity entity = new AiWorkflowEntity();
        entity.setWorkflowUuid(workflowUuid);
        entity.setTitle(vo.getTitle());
        entity.setRemark(vo.getRemark());
        entity.setIsPublic(vo.getIsPublic());
        entity.setIsEnable(true);
        entity.setIsDeleted(false);
        entity.setUserId(userId);

        // 智能路由相关字段(可选)
        if (vo.getDesc() != null) {
            entity.setDesc(vo.getDesc());
        }
        if (vo.getKeywords() != null) {
            entity.setKeywords(vo.getKeywords());
        }
        if (vo.getPriority() != null) {
            entity.setPriority(vo.getPriority());
        }

        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        aiWorkflowMapper.insert(entity);

        // 创建开始节点
        workflowNodeService.createStartNode(entity);

        // 转换为VO
        AiWorkflowVo result = new AiWorkflowVo();
        BeanUtils.copyProperties(entity, result);

        // 填充节点和边信息
        if (result.getId() != null) {
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listDtoByWfId(result.getId());
            result.setNodes(nodes);
            List<AiWorkflowEdgeVo> edges = workflowEdgeService.listDtoByWfId(result.getId());
            result.setEdges(edges);
        }

        return result;
    }

    /**
     * 复制工作流
     *
     * <p>复制工作流定义及其所有节点和连线,自动生成新的UUID</p>
     *
     * @param wfUuid 源工作流UUID
     * @return 新工作流VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowVo copy(String wfUuid) {
        // TODO: 添加频率限制(Redis锁)
        Long userId = SecurityUtil.getStaff_id();

        AiWorkflowEntity sourceWorkflow = getOrThrow(wfUuid);

        String newWorkflowUuid = UuidUtil.createShort();
        AiWorkflowEntity newWorkflow = new AiWorkflowEntity();
        newWorkflow.setWorkflowUuid(newWorkflowUuid);
        newWorkflow.setTitle(sourceWorkflow.getTitle() + "-copy");
        newWorkflow.setRemark(sourceWorkflow.getRemark());
        newWorkflow.setIsPublic(false); // 复制的工作流默认私有
        newWorkflow.setIsEnable(true);
        newWorkflow.setIsDeleted(false);
        newWorkflow.setUserId(userId);
        aiWorkflowMapper.insert(newWorkflow);

        // 复制节点和连线
        workflowNodeService.copyByWorkflowId(sourceWorkflow.getId(), newWorkflow.getId());
        workflowEdgeService.copyByWorkflowId(sourceWorkflow.getId(), newWorkflow.getId());

        // 转换为VO
        AiWorkflowVo vo = new AiWorkflowVo();
        BeanUtils.copyProperties(newWorkflow, vo);

        // 填充节点和边信息
        if (vo.getId() != null) {
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listDtoByWfId(vo.getId());
            vo.setNodes(nodes);
            List<AiWorkflowEdgeVo> edges = workflowEdgeService.listDtoByWfId(vo.getId());
            vo.setEdges(edges);
        }

        return vo;
    }

    /**
     * 设置工作流公开状态
     *
     * @param wfUuid 工作流UUID
     * @param isPublic 是否公开(true-公开,false-私有)
     */
    public void setPublic(String wfUuid, Boolean isPublic) {
        Long userId = SecurityUtil.getStaff_id();

        AiWorkflowEntity workflow = getOrThrow(wfUuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        // 更新工作流公开状态（在查询出的实体上直接修改）
        workflow.setIsPublic(isPublic);
        aiWorkflowMapper.updateById(workflow);
    }

    /**
     * 更新工作流
     * 对应AIDeepin: update(WorkflowUpdateReq req)
     * 对应前端: workflowService.workflowUpdate({id, title, remark, isPublic, nodes, edges, deleteNodes, deleteEdges})
     *
     * @param vo 工作流VO对象
     * @return 更新后的工作流VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowVo update(AiWorkflowVo vo) {
        Long userId = SecurityUtil.getStaff_id();

        if (vo.getId() == null || StringUtils.isBlank(vo.getTitle())) {
            throw new RuntimeException("工作流ID和标题不能为空");
        }

        // 查询现有工作流
        AiWorkflowEntity workflow = aiWorkflowMapper.selectById(vo.getId());
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + vo.getId());
        }

        // 权限检查
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        // 更新工作流基本信息（在查询出的实体上直接修改）
        workflow.setTitle(vo.getTitle());
        workflow.setRemark(vo.getRemark());
        if (vo.getIsPublic() != null) {
            workflow.setIsPublic(vo.getIsPublic());
        }
        // u_time, u_id, dbversion由MyBatis-Plus自动填充
        aiWorkflowMapper.updateById(workflow);

        // 更新节点和边：先删除再创建/更新，避免UUID冲突
        // 1. 先删除节点和边
        if (vo.getDeleteNodes() != null && !vo.getDeleteNodes().isEmpty()) {
            workflowNodeService.deleteNodes(vo.getId(), vo.getDeleteNodes());
        }
        if (vo.getDeleteEdges() != null && !vo.getDeleteEdges().isEmpty()) {
            workflowEdgeService.deleteEdges(vo.getId(), vo.getDeleteEdges());
        }
        // 2. 再创建/更新节点和边
        if (vo.getNodes() != null) {
            workflowNodeService.createOrUpdateNodes(vo.getId(), vo.getNodes());
        }
        if (vo.getEdges() != null) {
            workflowEdgeService.createOrUpdateEdges(vo.getId(), vo.getEdges());
        }

        // 返回更新后的工作流VO
        return getDtoByWorkflowId(vo.getId());
    }

    /**
     * 更新工作流基本信息
     *
     * @param vo 工作流VO对象(包含要更新的字段)
     * @return 更新后的工作流VO
     */
    public AiWorkflowVo updateBaseInfo(AiWorkflowVo vo) {
        Long userId = SecurityUtil.getStaff_id();

        if (vo == null || StringUtils.isAnyBlank(vo.getWorkflowUuid(), vo.getTitle())) {
            throw new RuntimeException("工作流UUID和标题不能为空");
        }

        AiWorkflowEntity workflow = getOrThrow(vo.getWorkflowUuid());
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        // 更新工作流基本信息（在查询出的实体上直接修改）
        workflow.setTitle(vo.getTitle());
        workflow.setRemark(vo.getRemark());
        if (vo.getIsPublic() != null) {
            workflow.setIsPublic(vo.getIsPublic());
        }

        // 智能路由相关字段（允许null，null表示不更新该字段）
        if (vo.getDesc() != null) {
            workflow.setDesc(vo.getDesc());
        }
        if (vo.getKeywords() != null) {
            workflow.setKeywords(vo.getKeywords());
        }
        if (vo.getPriority() != null) {
            workflow.setPriority(vo.getPriority());
        }

        aiWorkflowMapper.updateById(workflow);

        log.info("更新工作流基本信息成功, wfUuid: {}, title: {}, desc长度: {}, keywords: {}, priority: {}",
                vo.getWorkflowUuid(), vo.getTitle(),
                vo.getDesc() != null ? vo.getDesc().length() : 0,
                vo.getKeywords(), vo.getPriority());

        return getDtoByUuid(vo.getWorkflowUuid());
    }

    /**
     * 查询工作流(抛异常如果不存在)
     *
     * @param uuid 工作流UUID
     * @return 工作流实体
     */
    public AiWorkflowEntity getOrThrow(String uuid) {
        AiWorkflowEntity workflow = aiWorkflowMapper.selectByWorkflowUuid(uuid);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + uuid);
        }
        return workflow;
    }

    /**
     * 查询工作流VO
     *
     * @param uuid 工作流UUID
     * @return 工作流VO
     */
    public AiWorkflowVo getDtoByUuid(String uuid) {
        AiWorkflowEntity entity = aiWorkflowMapper.selectByWorkflowUuid(uuid);
        if (entity == null) {
            return null;
        }

        // 转换为VO
        AiWorkflowVo vo = new AiWorkflowVo();
        BeanUtils.copyProperties(entity, vo);

        // 填充节点和边信息
        if (vo.getId() != null) {
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listDtoByWfId(vo.getId());
            vo.setNodes(nodes);
            List<AiWorkflowEdgeVo> edges = workflowEdgeService.listDtoByWfId(vo.getId());
            vo.setEdges(edges);
        }

        return vo;
    }

    /**
     * 搜索用户的工作流
     *
     * @param keyword 关键词
     * @param isPublic 是否公开
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public Page<AiWorkflowVo> search(String keyword, Boolean isPublic, Integer currentPage, Integer pageSize) {
        Long userId = SecurityUtil.getStaff_id();

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowEntity> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        wrapper.eq(AiWorkflowEntity::getUserId, userId);
        wrapper.eq(AiWorkflowEntity::getIsDeleted, false);

        if (isPublic != null) {
            wrapper.eq(AiWorkflowEntity::getIsPublic, isPublic);
        }

        if (org.apache.commons.lang3.StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(AiWorkflowEntity::getTitle, keyword)
                    .or().like(AiWorkflowEntity::getRemark, keyword));
        }

        wrapper.orderByDesc(AiWorkflowEntity::getUTime);

        Page<AiWorkflowEntity> entityPage = aiWorkflowMapper.selectPage(
                new Page<>(currentPage, pageSize), wrapper
        );

        Page<AiWorkflowVo> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());

        List<AiWorkflowVo> voList = new ArrayList<>();
        for (AiWorkflowEntity entity : entityPage.getRecords()) {
            AiWorkflowVo vo = new AiWorkflowVo();
            BeanUtils.copyProperties(entity, vo);

            // 填充节点和边信息
            if (vo.getId() != null) {
                List<AiWorkflowNodeVo> nodes = workflowNodeService.listDtoByWfId(vo.getId());
                vo.setNodes(nodes);
                List<AiWorkflowEdgeVo> edges = workflowEdgeService.listDtoByWfId(vo.getId());
                vo.setEdges(edges);
            }

            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 搜索公开的工作流
     *
     * @param keyword 关键词
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public Page<AiWorkflowVo> searchPublic(String keyword, Integer currentPage, Integer pageSize) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowEntity> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        wrapper.eq(AiWorkflowEntity::getIsPublic, true); // true-公开
        wrapper.eq(AiWorkflowEntity::getIsDeleted, false);
        wrapper.eq(AiWorkflowEntity::getIsEnable, true); // true-启用

        if (org.apache.commons.lang3.StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(AiWorkflowEntity::getTitle, keyword)
                    .or().like(AiWorkflowEntity::getRemark, keyword));
        }

        wrapper.orderByDesc(AiWorkflowEntity::getUTime);

        Page<AiWorkflowEntity> entityPage = aiWorkflowMapper.selectPage(
                new Page<>(currentPage, pageSize), wrapper
        );

        Page<AiWorkflowVo> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());

        List<AiWorkflowVo> voList = new ArrayList<>();
        for (AiWorkflowEntity entity : entityPage.getRecords()) {
            AiWorkflowVo vo = new AiWorkflowVo();
            BeanUtils.copyProperties(entity, vo);

            // 填充节点和边信息
            if (vo.getId() != null) {
                List<AiWorkflowNodeVo> nodes = workflowNodeService.listDtoByWfId(vo.getId());
                vo.setNodes(nodes);
                List<AiWorkflowEdgeVo> edges = workflowEdgeService.listDtoByWfId(vo.getId());
                vo.setEdges(edges);
            }

            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 软删除工作流
     *
     * @param uuid 工作流UUID
     */
    public void softDelete(String uuid) {
        Long userId = SecurityUtil.getStaff_id();

        AiWorkflowEntity workflow = getOrThrow(uuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此工作流");
        }

        // 软删除工作流（在查询出的实体上直接修改）
        workflow.setIsDeleted(true);
        aiWorkflowMapper.updateById(workflow);
    }

    /**
     * 启用/禁用工作流
     * 启用状态：true-启用,false-禁用
     *
     * @param uuid 工作流UUID
     * @param enable 是否启用
     */
    public void enable(String uuid, Boolean enable) {
        Long userId = SecurityUtil.getStaff_id();

        if (enable == null) {
            throw new RuntimeException("启用状态不能为空");
        }

        AiWorkflowEntity workflow = getOrThrow(uuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        aiWorkflowMapper.updateEnableStatus(uuid, enable);
    }

    /**
     * 根据工作流ID查询工作流VO
     *
     * @param workflowId 工作流ID
     * @return 工作流VO
     */
    public AiWorkflowVo getDtoByWorkflowId(Long workflowId) {
        AiWorkflowEntity entity = aiWorkflowMapper.selectById(workflowId);
        if (entity == null) {
            return null;
        }

        // 转换为VO
        AiWorkflowVo vo = new AiWorkflowVo();
        BeanUtils.copyProperties(entity, vo);

        // 填充节点和边信息
        if (vo.getId() != null) {
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listDtoByWfId(vo.getId());
            vo.setNodes(nodes);
            List<AiWorkflowEdgeVo> edges = workflowEdgeService.listDtoByWfId(vo.getId());
            vo.setEdges(edges);
        }

        return vo;
    }

    /**
     * 查询可用的工作流列表（用于子工作流选择器）
     * 返回所有公开的工作流 + 当前用户自己的工作流
     *
     * @param userId 用户ID
     * @return 工作流列表
     */
    public List<AiWorkflowEntity> getAvailableWorkflows(Long userId) {
        return aiWorkflowMapper.selectAvailableWorkflows(userId);
    }

    // ==================== 智能路由新增方法 (2025-11-10) ====================

    /**
     * 查询用户可用的工作流 (用于智能路由)
     *
     * @param tenantCode 租户编码
     * @param userId 用户ID
     * @return 可用工作流Vo列表(含分类名称等扩展信息)
     */
    public List<AiWorkflowVo> getAvailableWorkflowsForRouting(String tenantCode, Long userId) {
        return aiWorkflowMapper.selectAvailableWorkflowsForRouting( userId);
    }

    /**
     * 查询用户所有工作流 (包括未发布的,用于管理页面)
     *
     * @param tenantCode 租户编码
     * @param userId 用户ID
     * @return 用户所有工作流列表
     */
    public List<AiWorkflowEntity> getAllUserWorkflows(String tenantCode, Long userId) {
        return aiWorkflowMapper.selectAllUserWorkflows( userId);
    }

    /**
     * 查询默认工作流 (兜底策略)
     *
     * @param tenantCode 租户编码
     * @return 默认工作流
     */
    public AiWorkflowEntity getDefaultWorkflow(String tenantCode) {
        return aiWorkflowMapper.selectDefaultWorkflow();
    }

    /**
     * 保存工作流
     * 如果当前是已发布状态,强制改为未发布
     *
     * @param workflow 工作流实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWorkflow(AiWorkflowEntity workflow) {
        if (workflow.getIsEnable() != null && workflow.getIsEnable()) {
            workflow.setIsEnable(false);
        }

        if (workflow.getId() == null) {
            aiWorkflowMapper.insert(workflow);
        } else {
            aiWorkflowMapper.updateById(workflow);
        }
    }

    /**
     * 更新测试运行时间
     *
     * @param workflowUuid 工作流UUID
     * @param tenantCode 租户编码
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTestTime(String workflowUuid, String tenantCode) {
        // 在方法开始时设置租户数据源上下文
        DataSourceHelper.use(tenantCode);

        AiWorkflowEntity workflow = aiWorkflowMapper.selectByWorkflowUuid(workflowUuid);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + workflowUuid);
        }

        workflow.setLastTestTime(java.time.LocalDateTime.now());
        aiWorkflowMapper.updateById(workflow);
    }

    /**
     * 发布工作流
     *
     * @param workflowUuid 工作流UUID
     */
    @Transactional(rollbackFor = Exception.class)
    public void publishWorkflow(String workflowUuid) {
        AiWorkflowEntity workflow = aiWorkflowMapper.selectByWorkflowUuid(workflowUuid);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + workflowUuid);
        }

        // 校验: 是否测试过
        if (workflow.getLastTestTime() == null) {
            throw new RuntimeException("请先执行测试运行,确认工作流正常后再发布");
        }

        // 校验: 测试时间是否在更新时间之后
        if (workflow.getUTime() != null &&
            workflow.getLastTestTime().isBefore(workflow.getUTime())) {
            throw new RuntimeException("工作流已修改,请重新测试运行后再发布");
        }

        // 发布
        workflow.setIsEnable(true);
        aiWorkflowMapper.updateById(workflow);
    }

    /**
     * 取消发布
     *
     * @param workflowUuid 工作流UUID
     */
    @Transactional(rollbackFor = Exception.class)
    public void unpublishWorkflow(String workflowUuid) {
        AiWorkflowEntity workflow = aiWorkflowMapper.selectByWorkflowUuid(workflowUuid);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + workflowUuid);
        }

        workflow.setIsEnable(false);
        aiWorkflowMapper.updateById(workflow);
    }
}
