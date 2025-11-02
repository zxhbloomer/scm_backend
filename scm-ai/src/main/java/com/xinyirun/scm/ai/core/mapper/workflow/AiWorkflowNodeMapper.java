package com.xinyirun.scm.ai.core.mapper.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.config.handler.FastjsonInputConfigTypeHandler;
import com.xinyirun.scm.ai.config.handler.FastjsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI工作流节点 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowNodeMapper extends BaseMapper<AiWorkflowNodeEntity> {

    /**
     * 按 workflow_id 和 uuid 查询节点（排除已删除）
     *
     * @param workflowId 工作流ID
     * @param uuid 节点UUID
     * @return 节点实体
     */
    @Select("""
        SELECT
            id,
            uuid,
            workflow_id AS workflowId,
            workflow_component_id AS workflowComponentId,
            title,
            remark,
            input_config,
            node_config,
            position_x AS positionX,
            position_y AS positionY,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE workflow_id = #{workflowId}
            AND uuid = #{uuid}
            AND is_deleted = 0
        LIMIT 1
    """)
    @Results({
        @Result(property = "inputConfig", column = "input_config",
                javaType = AiWfNodeInputConfigVo.class,
                typeHandler = FastjsonInputConfigTypeHandler.class),
        @Result(property = "nodeConfig", column = "node_config",
                javaType = JSONObject.class,
                typeHandler = FastjsonTypeHandler.class)
    })
    AiWorkflowNodeVo selectByWorkflowIdAndUuid(@Param("workflowId") Long workflowId,
                                                @Param("uuid") String uuid);

    /**
     * 按 workflow_id 查询所有节点（排除已删除）
     * 参考 BPoOrderMapper.selectPage 的写法，使用 @Results 指定 TypeHandler
     *
     * @param workflowId 工作流ID
     * @return 节点列表
     */
    @Select("""
        SELECT
            id,
            uuid,
            workflow_id AS workflowId,
            workflow_component_id AS workflowComponentId,
            title,
            remark,
            input_config,
            node_config,
            position_x AS positionX,
            position_y AS positionY,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE workflow_id = #{workflowId}
            AND is_deleted = 0
        ORDER BY c_time ASC
    """)
    @Results({
        @Result(property = "inputConfig", column = "input_config",
                javaType = AiWfNodeInputConfigVo.class,
                typeHandler = FastjsonInputConfigTypeHandler.class),
        @Result(property = "nodeConfig", column = "node_config",
                javaType = JSONObject.class,
                typeHandler = FastjsonTypeHandler.class)
    })
    List<AiWorkflowNodeVo> selectByWorkflowId(@Param("workflowId") Long workflowId);

    /**
     * 获取工作流的起始节点（按 workflow_component_id 查询）
     *
     * @param workflowId 工作流ID
     * @param componentId 组件ID（start 组件）
     * @return 起始节点
     */
    @Select("""
        SELECT
            id,
            uuid,
            workflow_id AS workflowId,
            workflow_component_id AS workflowComponentId,
            title,
            remark,
            input_config,
            node_config,
            position_x AS positionX,
            position_y AS positionY,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE workflow_id = #{workflowId}
            AND workflow_component_id = #{componentId}
            AND is_deleted = 0
        LIMIT 1
    """)
    @Results({
        @Result(property = "inputConfig", column = "input_config",
                javaType = AiWfNodeInputConfigVo.class,
                typeHandler = FastjsonInputConfigTypeHandler.class),
        @Result(property = "nodeConfig", column = "node_config",
                javaType = JSONObject.class,
                typeHandler = FastjsonTypeHandler.class)
    })
    AiWorkflowNodeVo selectStartNode(@Param("workflowId") Long workflowId,
                                      @Param("componentId") Long componentId);

    /**
     * 按 workflow_id 和 component_uuid 查询节点（使用 UUID 识别节点类型）
     *
     * @param workflowId 工作流ID
     * @param componentUuid 组件UUID
     * @return 节点VO
     */
    @Select("""
        SELECT
            n.id,
            n.uuid,
            n.workflow_id AS workflowId,
            n.workflow_component_id AS workflowComponentId,
            n.title,
            n.remark,
            n.input_config,
            n.node_config,
            n.position_x AS positionX,
            n.position_y AS positionY,
            n.is_deleted AS isDeleted,
            n.c_time AS cTime,
            n.c_id AS cId,
            n.u_time AS uTime,
            n.u_id AS uId,
            n.dbversion
        FROM ai_workflow_node n
        INNER JOIN ai_workflow_component c ON n.workflow_component_id = c.id
        WHERE n.workflow_id = #{workflowId}
            AND c.component_uuid = #{componentUuid}
            AND n.is_deleted = 0
        LIMIT 1
    """)
    @Results({
        @Result(property = "inputConfig", column = "input_config",
                javaType = AiWfNodeInputConfigVo.class,
                typeHandler = FastjsonInputConfigTypeHandler.class),
        @Result(property = "nodeConfig", column = "node_config",
                javaType = JSONObject.class,
                typeHandler = FastjsonTypeHandler.class)
    })
    AiWorkflowNodeVo selectNodeByComponentUuid(@Param("workflowId") Long workflowId,
                                                 @Param("componentUuid") String componentUuid);

    /**
     * 按 workflow_id 和 uuid 查询节点（包含已删除，用于复制后查询）
     *
     * @param workflowId 工作流ID
     * @param uuid 节点UUID
     * @return 节点实体
     */
    @Select("""
        SELECT
            id,
            uuid,
            workflow_id AS workflowId,
            workflow_component_id AS workflowComponentId,
            title,
            remark,
            input_config,
            node_config,
            position_x AS positionX,
            position_y AS positionY,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE workflow_id = #{workflowId}
            AND uuid = #{uuid}
        LIMIT 1
    """)
    @Results({
        @Result(property = "inputConfig", column = "input_config",
                javaType = AiWfNodeInputConfigVo.class,
                typeHandler = FastjsonInputConfigTypeHandler.class),
        @Result(property = "nodeConfig", column = "node_config",
                javaType = JSONObject.class,
                typeHandler = FastjsonTypeHandler.class)
    })
    AiWorkflowNodeVo selectByWorkflowIdAndUuidIncludeDeleted(@Param("workflowId") Long workflowId,
                                                              @Param("uuid") String uuid);
}
