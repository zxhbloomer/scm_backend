package com.xinyirun.scm.ai.bean.vo.workflow;

import lombok.Data;

/**
 * 工作流选项VO
 * 用于子工作流选择器的数据传输
 *
 * @author zxh
 * @since 2025-11-06
 */
@Data
public class WorkflowOptionVo {

    /**
     * 工作流UUID
     */
    private String workflowUuid;

    /**
     * 工作流名称
     */
    private String name;

    /**
     * 是否公开（1-公开，0-私有）
     */
    private Integer isPublic;

    /**
     * 创建者ID（用于前端分组）
     */
    private Long userId;

    /**
     * 工作流描述
     */
    private String description;
}
