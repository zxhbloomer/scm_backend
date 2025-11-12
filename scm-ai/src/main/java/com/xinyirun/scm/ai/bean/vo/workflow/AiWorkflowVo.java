package com.xinyirun.scm.ai.bean.vo.workflow;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI工作流VO类
 * 对应实体类:AiWorkflowEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiWorkflowVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工作流UUID(业务主键)
     */
    private String workflowUuid;

    /**
     * 工作流标题
     */
    private String title;

    /**
     * 工作流描述
     */
    private String remark;

    /**
     * 工作流详细描述(供LLM路由使用)
     * 对应数据库字段: desc
     */
    private String desc;

    /**
     * 关键词(逗号分隔)
     * 对应数据库字段: keywords
     */
    private String keywords;

    /**
     * 工作流分类(字典值)
     * 对应数据库字段: category
     */
    private String category;

    /**
     * 工作流分类名称(从字典表关联查询)
     * 对应数据库: s_dict_data.label
     */
    private String categoryName;

    /**
     * 优先级
     * 对应数据库字段: priority
     */
    private Integer priority;

    /**
     * 是否公开(false-否,true-是)
     */
    private Boolean isPublic;

    /**
     * 是否启用(false-禁用,true-启用)
     */
    private Boolean isEnable;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户UUID
     */
    private String userUuid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 节点列表
     */
    private List<AiWorkflowNodeVo> nodes;

    /**
     * 连接边列表
     */
    private List<AiWorkflowEdgeVo> edges;

    /**
     * 创建时间
     */
    private LocalDateTime cTime;

    /**
     * 修改时间
     */
    private LocalDateTime uTime;

    /**
     * 要删除的节点UUID列表
     */
    private List<String> deleteNodes;

    /**
     * 要删除的边UUID列表
     */
    private List<String> deleteEdges;
}
