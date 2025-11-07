package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI工作流运行时实例VO类
 * 对应实体类:AiWorkflowRuntimeEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiWorkflowRuntimeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 运行时UUID(业务主键)
     */
    private String runtimeUuid;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 工作流UUID
     */
    private String workflowUuid;

    /**
     * 对话ID,用于多轮对话上下文管理
     * 格式:tenantCode::uuid
     */
    private String conversationId;

    /**
     * 输入参数(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Jackson 的 ObjectNode
     * 字段名从input改为inputData，避免JSqlParser保留字冲突
     */
    private JSONObject inputData;

    /**
     * 输出结果(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Jackson 的 ObjectNode
     * 字段名从output改为outputData，避免JSqlParser保留字冲突
     */
    private JSONObject outputData;

    /**
     * 执行状态(1-运行中,2-成功,3-失败)
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusRemark;

    /**
     * 创建时间
     * 使用 Fastjson2 的 @JSONField 替代 Jackson 的 @JsonFormat
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cTime;
}
