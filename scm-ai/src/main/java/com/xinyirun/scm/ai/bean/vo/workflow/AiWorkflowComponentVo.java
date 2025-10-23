package com.xinyirun.scm.ai.bean.vo.workflow;

import lombok.Data;

/**
 * AI工作流组件VO类
 * 对应实体类:AiWorkflowComponentEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiWorkflowComponentVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 组件UUID(业务主键)
     */
    private String componentUuid;

    /**
     * 组件名称(英文标识)
     */
    private String name;

    /**
     * 组件标题(中文显示)
     */
    private String title;

    /**
     * 组件描述
     */
    private String remark;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    private Integer isEnable;

    /**
     * 显示顺序
     */
    private Integer displayOrder;
}
