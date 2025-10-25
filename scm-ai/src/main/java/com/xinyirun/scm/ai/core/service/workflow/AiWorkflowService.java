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
     * @param title 标题
     * @param remark 备注
     * @param isPublic 是否公开(false-私有,true-公开)
     * @return 工作流VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowVo add(String title, String remark, Boolean isPublic) {
        Long userId = SecurityUtil.getStaff_id();
        String workflowUuid = UuidUtil.createShort();

        AiWorkflowEntity entity = new AiWorkflowEntity();
        entity.setWorkflowUuid(workflowUuid);
        entity.setTitle(title);
        entity.setRemark(remark);
        entity.setIsPublic(isPublic);
        entity.setIsEnable(true);
        entity.setIsDeleted(false);
        entity.setUserId(userId);
        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(entity);

        // 创建开始节点
        workflowNodeService.createStartNode(entity);

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
        baseMapper.insert(newWorkflow);

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
        baseMapper.updateById(workflow);
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
        AiWorkflowEntity workflow = baseMapper.selectById(vo.getId());
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
        baseMapper.updateById(workflow);

        // 更新节点和边（参考aideepin WorkflowService.update()）
        if (vo.getNodes() != null) {
            workflowNodeService.createOrUpdateNodes(vo.getId(), vo.getNodes());
        }
        if (vo.getEdges() != null) {
            workflowEdgeService.createOrUpdateEdges(vo.getId(), vo.getEdges());
        }
        if (vo.getDeleteNodes() != null && !vo.getDeleteNodes().isEmpty()) {
            workflowNodeService.deleteNodes(vo.getId(), vo.getDeleteNodes());
        }
        if (vo.getDeleteEdges() != null && !vo.getDeleteEdges().isEmpty()) {
            workflowEdgeService.deleteEdges(vo.getId(), vo.getDeleteEdges());
        }

        // 返回更新后的工作流VO
        return getDtoByWorkflowId(vo.getId());
    }

    /**
     * 更新工作流基本信息
     *
     * @param wfUuid 工作流UUID
     * @param title 标题
     * @param remark 备注
     * @param isPublic 是否公开(false-私有,true-公开)
     * @return 更新后的工作流VO
     */
    public AiWorkflowVo updateBaseInfo(String wfUuid, String title, String remark, Boolean isPublic) {
        Long userId = SecurityUtil.getStaff_id();

        if (StringUtils.isAnyBlank(wfUuid, title)) {
            throw new RuntimeException("工作流UUID和标题不能为空");
        }

        AiWorkflowEntity workflow = getOrThrow(wfUuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        // 更新工作流基本信息（在查询出的实体上直接修改）
        workflow.setTitle(title);
        workflow.setRemark(remark);
        if (isPublic != null) {
            workflow.setIsPublic(isPublic);
        }
        baseMapper.updateById(workflow);

        return getDtoByUuid(wfUuid);
    }

    /**
     * 查询工作流(抛异常如果不存在)
     *
     * @param uuid 工作流UUID
     * @return 工作流实体
     */
    public AiWorkflowEntity getOrThrow(String uuid) {
        AiWorkflowEntity workflow = baseMapper.selectByWorkflowUuid(uuid);
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
        AiWorkflowEntity entity = baseMapper.selectByWorkflowUuid(uuid);
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

        Page<AiWorkflowEntity> entityPage = baseMapper.selectPage(
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

        Page<AiWorkflowEntity> entityPage = baseMapper.selectPage(
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
        baseMapper.updateById(workflow);
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

        baseMapper.updateEnableStatus(uuid, enable);
    }

    /**
     * 根据工作流ID查询工作流VO
     *
     * @param workflowId 工作流ID
     * @return 工作流VO
     */
    public AiWorkflowVo getDtoByWorkflowId(Long workflowId) {
        AiWorkflowEntity entity = baseMapper.selectById(workflowId);
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
}
