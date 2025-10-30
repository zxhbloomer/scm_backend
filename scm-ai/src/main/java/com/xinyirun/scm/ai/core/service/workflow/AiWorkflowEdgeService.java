package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowEdgeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowEdgeMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * AI工作流连接边Service
 *
 * <p>提供工作流节点连接关系的创建、复制、查询、更新、删除等功能</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiWorkflowEdgeService extends ServiceImpl<AiWorkflowEdgeMapper, AiWorkflowEdgeEntity> {

    @Resource
    private AiWorkflowEdgeMapper aiWorkflowEdgeMapper;

    @Lazy
    @Resource
    private AiWorkflowEdgeService self;

    /**
     * 按UUID查询边
     *
     * @param uuid 边UUID
     * @return 边实体
     */
    public AiWorkflowEdgeEntity getByUuid(String uuid) {
        return aiWorkflowEdgeMapper.selectByUuid(uuid);
    }

    /**
     * 查询工作流的所有连接边
     *
     * @param workflowId 工作流ID
     * @return 连接边列表
     */
    public List<AiWorkflowEdgeEntity> listByWorkflowId(Long workflowId) {
        return aiWorkflowEdgeMapper.selectByWorkflowId(workflowId);
    }

    /**
     * 查询工作流的所有连接边VO
     *
     * @param workflowId 工作流ID
     * @return 连接边VO列表
     */
    public List<AiWorkflowEdgeVo> listDtoByWfId(Long workflowId) {
        List<AiWorkflowEdgeEntity> edgeList = listByWorkflowId(workflowId);
        List<AiWorkflowEdgeVo> result = new ArrayList<>();
        for (AiWorkflowEdgeEntity entity : edgeList) {
            result.add(changeEdgeToDTO(entity));
        }
        return result;
    }

    /**
     * 复制工作流的所有连接边
     *
     * @param sourceWorkflowId 源工作流ID
     * @param targetWorkflowId 目标工作流ID
     * @return 新连接边列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AiWorkflowEdgeEntity> copyByWorkflowId(Long sourceWorkflowId, Long targetWorkflowId) {
        List<AiWorkflowEdgeEntity> result = new ArrayList<>();
        List<AiWorkflowEdgeEntity> sourceEdges = self.listByWorkflowId(sourceWorkflowId);

        for (AiWorkflowEdgeEntity sourceEdge : sourceEdges) {
            AiWorkflowEdgeEntity newEdge = self.copyEdge(targetWorkflowId, sourceEdge);
            result.add(newEdge);
        }

        return result;
    }

    /**
     * 复制单个连接边
     *
     * @param targetWorkflowId 目标工作流ID
     * @param sourceEdge 源连接边
     * @return 新连接边
     */
    public AiWorkflowEdgeEntity copyEdge(Long targetWorkflowId, AiWorkflowEdgeEntity sourceEdge) {
        AiWorkflowEdgeEntity newEdge = new AiWorkflowEdgeEntity();
        BeanUtils.copyProperties(sourceEdge, newEdge, "id", "uuid", "cTime", "uTime", "cId", "uId", "dbversion");

        newEdge.setUuid(UuidUtil.createShort());
        newEdge.setWorkflowId(targetWorkflowId);
        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        aiWorkflowEdgeMapper.insert(newEdge);

        return aiWorkflowEdgeMapper.selectById(newEdge.getId());
    }

    /**
     * 创建或更新连接边列表
     *
     * @param workflowId 工作流ID
     * @param edges 连接边VO列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateEdges(Long workflowId, List<AiWorkflowEdgeVo> edges) {
        for (AiWorkflowEdgeVo edgeVo : edges) {
            AiWorkflowEdgeEntity old = self.getByUuid(edgeVo.getUuid());
            if (old != null) {
                // 更新：在查询出的实体上直接修改
                if (!old.getWorkflowId().equals(workflowId)) {
                    log.error("该边不属于指定的工作流,保存失败,workflowId:{},old workflowId:{},edge uuid:{}",
                            workflowId, old.getWorkflowId(), edgeVo.getUuid());
                    throw new RuntimeException("该边不属于指定的工作流");
                }
                BeanUtils.copyProperties(edgeVo, old, "id", "cTime", "cId", "uTime", "uId", "dbversion");
                old.setWorkflowId(workflowId);
                aiWorkflowEdgeMapper.updateById(old);
                log.info("更新边,uuid:{},source:{},target:{}",
                        edgeVo.getUuid(), edgeVo.getSourceNodeUuid(), edgeVo.getTargetNodeUuid());
            } else {
                // 新增
                AiWorkflowEdgeEntity entity = new AiWorkflowEdgeEntity();
                BeanUtils.copyProperties(edgeVo, entity);
                entity.setWorkflowId(workflowId);
                entity.setId(null);
                aiWorkflowEdgeMapper.insert(entity);
                log.info("新增边,uuid:{},source:{},target:{}",
                        edgeVo.getUuid(), edgeVo.getSourceNodeUuid(), edgeVo.getTargetNodeUuid());
            }
        }
    }

    /**
     * 删除连接边列表
     *
     * @param workflowId 工作流ID
     * @param uuids 连接边UUID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEdges(Long workflowId, List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return;
        }

        for (String uuid : uuids) {
            AiWorkflowEdgeEntity edge = self.getByUuid(uuid);
            if (edge == null) {
                continue;
            }

            if (!edge.getWorkflowId().equals(workflowId)) {
                log.error("该边不属于指定的工作流,删除失败,workflowId:{},edge workflowId:{}",
                        workflowId, edge.getWorkflowId());
                throw new RuntimeException("该边不属于指定的工作流");
            }

            // 软删除边（在查询出的实体上直接修改）
            edge.setIsDeleted(true);
            aiWorkflowEdgeMapper.updateById(edge);
        }
    }

    /**
     * 将边实体转换为VO
     *
     * @param entity 边实体
     * @return 边VO
     */
    private AiWorkflowEdgeVo changeEdgeToDTO(AiWorkflowEdgeEntity entity) {
        AiWorkflowEdgeVo vo = new AiWorkflowEdgeVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
