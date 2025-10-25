package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowNodeMapper;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        Long startComponentId = workflowComponentService.getStartComponent().getId();
        return baseMapper.selectStartNode(workflowId, startComponentId);
    }

    /**
     * 按UUID查询节点
     *
     * @param workflowId 工作流ID
     * @param uuid 节点UUID
     * @return 节点实体
     */
    public AiWorkflowNodeEntity getByUuid(Long workflowId, String uuid) {
        return baseMapper.selectByWorkflowIdAndUuid(workflowId, uuid);
    }

    /**
     * 查询工作流的所有节点
     *
     * @param workflowId 工作流ID
     * @return 节点列表
     */
    public List<AiWorkflowNodeEntity> listByWorkflowId(Long workflowId) {
        return baseMapper.selectByWorkflowId(workflowId);
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
            AiWorkflowNodeVo vo = new AiWorkflowNodeVo();
            BeanUtils.copyProperties(entity, vo);
            result.add(vo);
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

        return baseMapper.selectByWorkflowIdAndUuidIncludeDeleted(targetWorkflowId, newNode.getUuid());
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
            AiWorkflowNodeEntity old = self.getByUuid(workflowId, nodeVo.getUuid());
            if (old != null) {
                // 更新：在查询出的实体上直接修改
                if (!old.getWorkflowId().equals(workflowId)) {
                    log.error("节点不属于指定的工作流,保存失败,workflowId:{},old workflowId:{},node uuid:{}",
                            workflowId, old.getWorkflowId(), nodeVo.getUuid());
                    throw new RuntimeException("节点不属于指定的工作流");
                }

                // null 字段设置数据库 default 值
                if (nodeVo.getPositionX() == null) {
                    nodeVo.setPositionX(new BigDecimal("0"));
                }
                if (nodeVo.getPositionY() == null) {
                    nodeVo.setPositionY(new BigDecimal("0"));
                }

                // 在查询出的实体上复制 VO 字段
                BeanUtils.copyProperties(nodeVo, old, "id", "cTime", "cId", "uTime", "uId", "dbversion");
                old.setWorkflowId(workflowId);
                baseMapper.updateById(old);
                log.info("更新节点,uuid:{}", nodeVo.getUuid());
            } else {
                // 新增
                AiWorkflowNodeEntity entity = new AiWorkflowNodeEntity();
                BeanUtils.copyProperties(nodeVo, entity);
                entity.setWorkflowId(workflowId);
                entity.setId(null);
                entity.setIsDeleted(false); // 显式设置is_deleted为false（MySQL需要）

                // 处理 position 字段：如果为 null 则使用数据库 default 值 0
                if (entity.getPositionX() == null) {
                    entity.setPositionX(new BigDecimal("0"));
                }
                if (entity.getPositionY() == null) {
                    entity.setPositionY(new BigDecimal("0"));
                }

                baseMapper.insert(entity);
                log.info("新增节点,uuid:{}", nodeVo.getUuid());
            }
        }
    }

    /**
     * 删除节点列表
     *
     * @param workflowId 工作流ID
     * @param uuids 节点UUID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNodes(Long workflowId, List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return;
        }

        Long startComponentId = workflowComponentService.getStartComponent().getId();

        for (String uuid : uuids) {
            AiWorkflowNodeEntity node = self.getByUuid(workflowId, uuid);
            if (node == null) {
                continue;
            }

            if (!node.getWorkflowId().equals(workflowId)) {
                log.error("节点不属于指定的工作流,删除失败,workflowId:{},node workflowId:{}",
                        workflowId, node.getWorkflowId());
                throw new RuntimeException("节点不属于指定的工作流");
            }

            if (startComponentId.equals(node.getWorkflowComponentId())) {
                log.warn("开始节点不能删除,uuid:{}", uuid);
                continue;
            }

            // 软删除节点（在查询出的实体上直接修改）
            node.setIsDeleted(true);
            baseMapper.updateById(node);
        }
    }

    /**
     * 创建开始节点
     * 严格参考 aideepin 的 WorkflowNodeService.createStartNode 方法
     *
     * @param workflow 工作流实体
     * @return 开始节点
     */
    public AiWorkflowNodeEntity createStartNode(AiWorkflowEntity workflow) {
        Long startComponentId = workflowComponentService.getStartComponent().getId();

        // 创建用户输入参数定义（参考 aideepin 的 WfNodeIOText）
        AiWfNodeIOVo userInputDef = new AiWfNodeIOVo();
        userInputDef.setUuid(UuidUtil.createShort());
        userInputDef.setType(1); // TEXT 类型
        userInputDef.setName("var_user_input");
        userInputDef.setTitle("用户输入");
        userInputDef.setRequired(false);
        userInputDef.setMaxLength(1000);

        // 创建输入配置（参考 aideepin 的 WfNodeInputConfig）
        AiWfNodeInputConfigVo inputConfig = new AiWfNodeInputConfigVo();
        List<AiWfNodeIOVo> userInputs = new ArrayList<>();
        userInputs.add(userInputDef);
        inputConfig.setUserInputs(userInputs);
        inputConfig.setRefInputs(new ArrayList<>());

        // 创建开始节点
        AiWorkflowNodeEntity node = new AiWorkflowNodeEntity();
        node.setUuid(UuidUtil.createShort());
        node.setWorkflowId(workflow.getId());
        node.setWorkflowComponentId(startComponentId);
        node.setTitle("开始");
        node.setRemark("用户输入");
        node.setIsDeleted(false); // 显式设置is_deleted为false（MySQL需要）
        node.setInputConfig(inputConfig); // 使用强类型 bean

        // 初始化节点配置为空对象
        node.setNodeConfig(new JSONObject());

        // 设置位置为 0，前端会把 0 当作未设置，使用默认值 (10, 50)
        // 参考 aideepin 的 WorkflowNodeService.createStartNode 方法
        // aideepin 使用 PostgreSQL，数据库默认值 0 会自动填充
        // scm-ai 使用 MySQL + MyBatis Plus，需要显式设置为 0
        // 前端逻辑：0 || 10 → 10 (JavaScript 中 0 是 falsy 值)
        node.setPositionX(new BigDecimal("0"));
        node.setPositionY(new BigDecimal("0"));

        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(node);

        return node;
    }

}
