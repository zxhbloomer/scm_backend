package com.xinyirun.scm.ai.bean.vo.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
     * 输入参数(JSON格式)
     */
    private ObjectNode input;

    /**
     * 输出结果(JSON格式)
     */
    private ObjectNode output;

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
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cTime;
}
