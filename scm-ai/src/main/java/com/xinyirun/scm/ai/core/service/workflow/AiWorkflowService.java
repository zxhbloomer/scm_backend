package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowEdgeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowMapper;
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
     * @param isPublic 是否公开(0-私有,1-公开)
     * @param userId 用户ID
     * @return 工作流VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowVo add(String title, String remark, Integer isPublic, Long userId) {
        String workflowUuid = UuidUtil.createShort();

        AiWorkflowEntity entity = new AiWorkflowEntity();
        entity.setWorkflowUuid(workflowUuid);
        entity.setTitle(title);
        entity.setRemark(remark);
        entity.setIsPublic(isPublic);
        entity.setIsEnable(1);
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
     * @param userId 当前用户ID
     * @return 新工作流VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowVo copy(String wfUuid, Long userId) {
        // TODO: 添加频率限制(Redis锁)

        AiWorkflowEntity sourceWorkflow = getOrThrow(wfUuid);

        String newWorkflowUuid = UuidUtil.createShort();
        AiWorkflowEntity newWorkflow = new AiWorkflowEntity();
        newWorkflow.setWorkflowUuid(newWorkflowUuid);
        newWorkflow.setTitle(sourceWorkflow.getTitle() + "-copy");
        newWorkflow.setRemark(sourceWorkflow.getRemark());
        newWorkflow.setIsPublic(0); // 复制的工作流默认私有
        newWorkflow.setIsEnable(1);
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
     * @param isPublic 是否公开(0-私有,1-公开)
     * @param userId 当前用户ID
     */
    public void setPublic(String wfUuid, Integer isPublic, Long userId) {
        AiWorkflowEntity workflow = getOrThrow(wfUuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        AiWorkflowEntity updateObj = new AiWorkflowEntity();
        updateObj.setId(workflow.getId());
        updateObj.setIsPublic(isPublic);
        baseMapper.updateById(updateObj);
    }

    /**
     * 更新工作流基本信息
     *
     * @param wfUuid 工作流UUID
     * @param title 标题
     * @param remark 备注
     * @param isPublic 是否公开
     * @param userId 当前用户ID
     * @return 更新后的工作流VO
     */
    public AiWorkflowVo updateBaseInfo(String wfUuid, String title, String remark, Integer isPublic, Long userId) {
        if (StringUtils.isAnyBlank(wfUuid, title)) {
            throw new RuntimeException("工作流UUID和标题不能为空");
        }

        AiWorkflowEntity workflow = getOrThrow(wfUuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        AiWorkflowEntity updateObj = new AiWorkflowEntity();
        updateObj.setId(workflow.getId());
        updateObj.setTitle(title);
        updateObj.setRemark(remark);
        if (isPublic != null) {
            updateObj.setIsPublic(isPublic);
        }
        baseMapper.updateById(updateObj);

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
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public Page<AiWorkflowVo> search(String keyword, Integer isPublic, Long userId, Integer currentPage, Integer pageSize) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowEntity> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        wrapper.eq(AiWorkflowEntity::getUserId, userId);
        wrapper.eq(AiWorkflowEntity::getIsDeleted, 0);

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

        wrapper.eq(AiWorkflowEntity::getIsPublic, 1); // 1-公开
        wrapper.eq(AiWorkflowEntity::getIsDeleted, 0);
        wrapper.eq(AiWorkflowEntity::getIsEnable, 1); // 1-启用

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
     * @param userId 当前用户ID
     */
    public void softDelete(String uuid, Long userId) {
        AiWorkflowEntity workflow = getOrThrow(uuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此工作流");
        }

        AiWorkflowEntity updateObj = new AiWorkflowEntity();
        updateObj.setId(workflow.getId());
        updateObj.setIsDeleted(1);
        baseMapper.updateById(updateObj);
    }

    /**
     * 启用/禁用工作流
     * 启用状态：0-禁用,1-启用
     *
     * @param uuid 工作流UUID
     * @param enable 是否启用
     * @param userId 当前用户ID
     */
    public void enable(String uuid, Integer enable, Long userId) {
        if (enable == null) {
            throw new RuntimeException("启用状态不能为空");
        }

        AiWorkflowEntity workflow = getOrThrow(uuid);
        if (!workflow.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此工作流");
        }

        baseMapper.updateEnableStatus(uuid, enable);
    }
}
