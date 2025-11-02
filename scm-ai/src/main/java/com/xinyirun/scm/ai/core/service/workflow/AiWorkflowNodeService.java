package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowNodeMapper;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import static com.xinyirun.scm.ai.workflow.WorkflowConstants.COMPONENT_UUID_START;
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

    @Resource
    private AiWorkflowNodeMapper aiWorkflowNodeMapper;

    @Lazy
    @Resource
    private AiWorkflowNodeService self;

    @Resource
    private AiWorkflowComponentService workflowComponentService;

    /**
     * 获取工作流的开始节点
     *
     * @param workflowId 工作流ID
     * @return 开始节点VO
     */
    public AiWorkflowNodeVo getStartNode(Long workflowId) {
        // 使用 UUID 常量来识别开始节点
        return aiWorkflowNodeMapper.selectNodeByComponentUuid(workflowId, COMPONENT_UUID_START);
    }

    /**
     * 按UUID查询节点
     *
     * @param workflowId 工作流ID
     * @param uuid 节点UUID
     * @return 节点VO
     */
    public AiWorkflowNodeVo getByUuid(Long workflowId, String uuid) {
        return aiWorkflowNodeMapper.selectByWorkflowIdAndUuid(workflowId, uuid);
    }

    /**
     * 查询工作流的所有节点
     *
     * @param workflowId 工作流ID
     * @return 节点VO列表
     */
    public List<AiWorkflowNodeVo> listByWorkflowId(Long workflowId) {
        return aiWorkflowNodeMapper.selectByWorkflowId(workflowId);
    }

    /**
     * 查询工作流的所有节点VO
     *
     * @param workflowId 工作流ID
     * @return 节点VO列表
     */
    public List<AiWorkflowNodeVo> listDtoByWfId(Long workflowId) {
        return listByWorkflowId(workflowId);
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
        List<AiWorkflowNodeVo> sourceNodes = self.listByWorkflowId(sourceWorkflowId);

        for (AiWorkflowNodeVo sourceNode : sourceNodes) {
            // VO转Entity
            AiWorkflowNodeEntity entity = new AiWorkflowNodeEntity();
            BeanUtils.copyProperties(sourceNode, entity);
            // JSON对象字段需要转String
            if (sourceNode.getInputConfig() != null) {
                entity.setInputConfig(JSONObject.toJSONString(sourceNode.getInputConfig()));
            }
            if (sourceNode.getNodeConfig() != null) {
                entity.setNodeConfig(JSONObject.toJSONString(sourceNode.getNodeConfig()));
            }

            AiWorkflowNodeEntity newNode = self.copyNode(targetWorkflowId, entity);
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
        aiWorkflowNodeMapper.insert(newNode);

        // Mapper返回VO，需要转换为Entity
        AiWorkflowNodeVo vo = aiWorkflowNodeMapper.selectByWorkflowIdAndUuidIncludeDeleted(targetWorkflowId, newNode.getUuid());
        AiWorkflowNodeEntity result = new AiWorkflowNodeEntity();
        BeanUtils.copyProperties(vo, result);
        // JSON对象字段需要转String
        if (vo.getInputConfig() != null) {
            result.setInputConfig(JSONObject.toJSONString(vo.getInputConfig()));
        }
        if (vo.getNodeConfig() != null) {
            result.setNodeConfig(JSONObject.toJSONString(vo.getNodeConfig()));
        }
        return result;
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
            AiWorkflowNodeVo old = self.getByUuid(workflowId, nodeVo.getUuid());
            if (old != null) {
                // 更新：VO转Entity后更新
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

                // VO转Entity
                AiWorkflowNodeEntity entity = aiWorkflowNodeMapper.selectById(old.getId());
                BeanUtils.copyProperties(nodeVo, entity, "id", "cTime", "cId", "uTime", "uId", "dbversion");
                // JSON对象字段需要转String
                if (nodeVo.getInputConfig() != null) {
                    entity.setInputConfig(JSONObject.toJSONString(nodeVo.getInputConfig()));
                }
                if (nodeVo.getNodeConfig() != null) {
                    entity.setNodeConfig(JSONObject.toJSONString(nodeVo.getNodeConfig()));
                }
                entity.setWorkflowId(workflowId);
                aiWorkflowNodeMapper.updateById(entity);
                log.info("更新节点,uuid:{}", nodeVo.getUuid());
            } else {
                // 新增：VO转Entity
                AiWorkflowNodeEntity entity = new AiWorkflowNodeEntity();
                BeanUtils.copyProperties(nodeVo, entity);
                // JSON对象字段需要转String
                if (nodeVo.getInputConfig() != null) {
                    entity.setInputConfig(JSONObject.toJSONString(nodeVo.getInputConfig()));
                }
                if (nodeVo.getNodeConfig() != null) {
                    entity.setNodeConfig(JSONObject.toJSONString(nodeVo.getNodeConfig()));
                }
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

                aiWorkflowNodeMapper.insert(entity);
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

        // 使用 UUID 而不是 ID 来识别开始节点
        String startComponentUuid = workflowComponentService.getStartComponent().getComponentUuid();

        for (String uuid : uuids) {
            AiWorkflowNodeVo node = self.getByUuid(workflowId, uuid);
            if (node == null) {
                continue;
            }

            if (!node.getWorkflowId().equals(workflowId)) {
                log.error("节点不属于指定的工作流,删除失败,workflowId:{},node workflowId:{}",
                        workflowId, node.getWorkflowId());
                throw new RuntimeException("节点不属于指定的工作流");
            }

            // 使用 component UUID 判断是否为开始节点
            // 通过 workflowComponentId 查询 component 获取其 UUID
            if (node.getWorkflowComponentId() != null) {
                AiWorkflowComponentEntity component = workflowComponentService.getById(node.getWorkflowComponentId());
                if (component != null && startComponentUuid.equals(component.getComponentUuid())) {
                    log.warn("开始节点不能删除,uuid:{}", uuid);
                    continue;
                }
            }

            // 软删除节点（VO转Entity后更新）
            AiWorkflowNodeEntity entity = aiWorkflowNodeMapper.selectById(node.getId());
            entity.setIsDeleted(true);
            aiWorkflowNodeMapper.updateById(entity);
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
        node.setInputConfig(JSONObject.toJSONString(inputConfig)); // JSON对象转String

        // 初始化节点配置为空对象
        node.setNodeConfig("{}");

        // 设置位置为 0，前端会把 0 当作未设置，使用默认值 (10, 50)
        // 参考 aideepin 的 WorkflowNodeService.createStartNode 方法
        // aideepin 使用 PostgreSQL，数据库默认值 0 会自动填充
        // scm-ai 使用 MySQL + MyBatis Plus，需要显式设置为 0
        // 前端逻辑：0 || 10 → 10 (JavaScript 中 0 是 falsy 值)
        node.setPositionX(new BigDecimal("0"));
        node.setPositionY(new BigDecimal("0"));

        // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
        aiWorkflowNodeMapper.insert(node);

        return node;
    }

}
