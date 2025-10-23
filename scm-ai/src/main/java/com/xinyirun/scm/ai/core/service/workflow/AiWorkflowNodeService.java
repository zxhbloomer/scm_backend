package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowNodeMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI工作流节点Service
 *
 * <p>提供节点的创建、复制、查询、更新、删除等功能</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiWorkflowNodeService extends ServiceImpl<AiWorkflowNodeMapper, AiWorkflowNodeEntity> {

    @Lazy
    @Resource
    private AiWorkflowNodeService self;

    @Resource
    private AiWorkflowComponentService workflowComponentService;

    /**
     * 获取工作流的开始节点
     *
     * @param workflowId 工作流ID
     * @return 开始节点
     */
    public AiWorkflowNodeEntity getStartNode(Long workflowId) {
        return baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowNodeEntity>()
                        .eq(AiWorkflowNodeEntity::getWorkflowId, workflowId)
                        .eq(AiWorkflowNodeEntity::getWorkflowComponentId,
                            workflowComponentService.getStartComponent().getId())
                        .eq(AiWorkflowNodeEntity::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
    }

    /**
     * 按UUID查询节点
     *
     * @param workflowId 工作流ID
     * @param nodeUuid 节点UUID
     * @return 节点实体
     */
    public AiWorkflowNodeEntity getByUuid(Long workflowId, String nodeUuid) {
        return baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowNodeEntity>()
                        .eq(AiWorkflowNodeEntity::getWorkflowId, workflowId)
                        .eq(AiWorkflowNodeEntity::getNodeUuid, nodeUuid)
                        .eq(AiWorkflowNodeEntity::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
    }

    /**
     * 查询工作流的所有节点
     *
     * @param workflowId 工作流ID
     * @return 节点列表
     */
    public List<AiWorkflowNodeEntity> listByWorkflowId(Long workflowId) {
        return baseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowNodeEntity>()
                        .eq(AiWorkflowNodeEntity::getWorkflowId, workflowId)
                        .eq(AiWorkflowNodeEntity::getIsDeleted, 0)
                        .orderByAsc(AiWorkflowNodeEntity::getCTime)
        );
    }

    /**
     * 查询工作流的所有节点VO
     *
     * @param workflowId 工作流ID
     * @return 节点VO列表
     */
    public List<AiWorkflowNodeVo> listDtoByWfId(Long workflowId) {
        List<AiWorkflowNodeEntity> nodeList = listByWorkflowId(workflowId);
        List<AiWorkflowNodeVo> result = new ArrayList<>();
        for (AiWorkflowNodeEntity entity : nodeList) {
            result.add(changeNodeToDTO(entity));
        }
        return result;
    }

    /**
     * 复制工作流的所有节点
     *
     * @param sourceWorkflowId 源工作流ID
     * @param targetWorkflowId 目标工作流ID
     * @return 新节点列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AiWorkflowNodeEntity> copyByWorkflowId(Long sourceWorkflowId, Long targetWorkflowId) {
        List<AiWorkflowNodeEntity> result = new ArrayList<>();
        List<AiWorkflowNodeEntity> sourceNodes = self.listByWorkflowId(sourceWorkflowId);

        for (AiWorkflowNodeEntity sourceNode : sourceNodes) {
            AiWorkflowNodeEntity newNode = self.copyNode(targetWorkflowId, sourceNode);
            result.add(newNode);
        }

        return result;
    }

    /**
     * 复制单个节点
     *
     * @param targetWorkflowId 目标工作流ID
     * @param sourceNode 源节点
     * @return 新节点
     */
    public AiWorkflowNodeEntity copyNode(Long targetWorkflowId, AiWorkflowNodeEntity sourceNode) {
        AiWorkflowNodeEntity newNode = new AiWorkflowNodeEntity();
        BeanUtils.copyProperties(sourceNode, newNode, "id", "cTime", "uTime", "cId", "uId", "dbversion");
        newNode.setWorkflowId(targetWorkflowId);
        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(newNode);

        return baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowNodeEntity>()
                        .eq(AiWorkflowNodeEntity::getWorkflowId, targetWorkflowId)
                        .eq(AiWorkflowNodeEntity::getNodeUuid, newNode.getNodeUuid())
                        .eq(AiWorkflowNodeEntity::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
    }

    /**
     * 创建或更新节点列表
     *
     * @param workflowId 工作流ID
     * @param nodes 节点VO列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateNodes(Long workflowId, List<AiWorkflowNodeVo> nodes) {
        for (AiWorkflowNodeVo nodeVo : nodes) {
            AiWorkflowNodeEntity entity = new AiWorkflowNodeEntity();
            BeanUtils.copyProperties(nodeVo, entity);
            entity.setWorkflowId(workflowId);

            AiWorkflowNodeEntity old = self.getByUuid(workflowId, nodeVo.getNodeUuid());
            if (old != null) {
                if (!old.getWorkflowId().equals(workflowId)) {
                    log.error("节点不属于指定的工作流,保存失败,workflowId:{},old workflowId:{},node uuid:{}",
                            workflowId, old.getWorkflowId(), nodeVo.getNodeUuid());
                    throw new RuntimeException("节点不属于指定的工作流");
                }
                entity.setId(old.getId());
                baseMapper.updateById(entity);
                log.info("更新节点,uuid:{}", nodeVo.getNodeUuid());
            } else {
                entity.setId(null);
                baseMapper.insert(entity);
                log.info("新增节点,uuid:{}", nodeVo.getNodeUuid());
            }
        }
    }

    /**
     * 删除节点列表
     *
     * @param workflowId 工作流ID
     * @param nodeUuids 节点UUID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNodes(Long workflowId, List<String> nodeUuids) {
        if (nodeUuids == null || nodeUuids.isEmpty()) {
            return;
        }

        Long startComponentId = workflowComponentService.getStartComponent().getId();

        for (String nodeUuid : nodeUuids) {
            AiWorkflowNodeEntity node = self.getByUuid(workflowId, nodeUuid);
            if (node == null) {
                continue;
            }

            if (!node.getWorkflowId().equals(workflowId)) {
                log.error("节点不属于指定的工作流,删除失败,workflowId:{},node workflowId:{}",
                        workflowId, node.getWorkflowId());
                throw new RuntimeException("节点不属于指定的工作流");
            }

            if (startComponentId.equals(node.getWorkflowComponentId())) {
                log.warn("开始节点不能删除,uuid:{}", nodeUuid);
                continue;
            }

            AiWorkflowNodeEntity updateObj = new AiWorkflowNodeEntity();
            updateObj.setId(node.getId());
            updateObj.setIsDeleted(1);
            baseMapper.updateById(updateObj);
        }
    }

    /**
     * 创建开始节点
     *
     * @param workflow 工作流实体
     * @return 开始节点
     */
    public AiWorkflowNodeEntity createStartNode(AiWorkflowEntity workflow) {
        Long startComponentId = workflowComponentService.getStartComponent().getId();

        AiWorkflowNodeEntity node = new AiWorkflowNodeEntity();
        node.setNodeUuid(UuidUtil.createShort());
        node.setWorkflowId(workflow.getId());
        node.setWorkflowComponentId(startComponentId);
        node.setName("start");
        node.setRemark("用户输入");

        // 初始化输入配置
        Map<String, Object> inputConfig = new HashMap<>();
        List<Map<String, Object>> userInputs = new ArrayList<>();
        Map<String, Object> userInput = new HashMap<>();
        userInput.put("uuid", UuidUtil.createShort());
        userInput.put("name", "var_user_input");
        userInput.put("title", "用户输入");
        userInput.put("type", 1); // TEXT类型
        userInput.put("required", false);
        userInput.put("maxLength", 1000);
        userInputs.add(userInput);

        inputConfig.put("userInputs", userInputs);
        inputConfig.put("refInputs", new ArrayList<>());
        node.setInputConfig(inputConfig);

        // 初始化节点配置为空对象
        node.setNodeConfig(new HashMap<>());

        // 设置默认位置
        node.setPositionX(new BigDecimal("100"));
        node.setPositionY(new BigDecimal("100"));

        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(node);

        return node;
    }

    /**
     * 将节点实体转换为VO
     *
     * @param entity 节点实体
     * @return 节点VO
     */
    private AiWorkflowNodeVo changeNodeToDTO(AiWorkflowNodeEntity entity) {
        AiWorkflowNodeVo vo = new AiWorkflowNodeVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
