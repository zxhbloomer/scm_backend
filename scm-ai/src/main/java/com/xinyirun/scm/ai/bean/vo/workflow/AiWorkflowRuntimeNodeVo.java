package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * AI工作流运行时节点VO类
 * 对应实体类:AiWorkflowRuntimeNodeEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiWorkflowRuntimeNodeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 运行时节点UUID(业务主键)
     */
    private String runtimeNodeUuid;

    /**
     * 运行时实例ID
     */
    private Long workflowRuntimeId;

    /**
     * 节点ID
     */
    private Long nodeId;

    /**
     * 节点输入(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Jackson 的 ObjectNode
     * 字段名从input改为inputData，避免JSqlParser保留字冲突
     */
    private JSONObject inputData;

    /**
     * 节点输出(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Jackson 的 ObjectNode
     * 字段名从output改为outputData，避免JSqlParser保留字冲突
     */
    private JSONObject outputData;

    /**
     * 执行状态(1-等待中,2-运行中,3-成功,4-失败)
     */
    private Integer status;
}
