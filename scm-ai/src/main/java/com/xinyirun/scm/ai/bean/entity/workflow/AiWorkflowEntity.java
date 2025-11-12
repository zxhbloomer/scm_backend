package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI工作流定义实体类
 * 对应数据表：ai_workflow
 *
 * 功能说明：存储AI工作流的定义信息，包括标题、描述、公开状态等
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_workflow")
public class AiWorkflowEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工作流UUID(业务主键)
     */
    @TableField("workflow_uuid")
    private String workflowUuid;

    /**
     * 工作流标题
     */
    @TableField("title")
    private String title;

    /**
     * 工作流描述说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否公开(0-私有,1-公开)
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    @TableField("is_enable")
    private Boolean isEnable;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本(乐观锁)
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

    // ==================== 智能路由新增字段 (2025-11-10) ====================

    /**
     * 详细描述,供LLM理解适用场景
     * 示例: "专门处理供应链管理相关问题,包括订单查询、库存管理等。
     *       适用场景: 用户询问具体的SCM业务操作或数据查询。
     *       不适用: 通用知识问答、闲聊。"
     * 数据库字段: desc (MySQL保留关键字,需反引号)
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 关键词,逗号分隔
     * 示例: "订单,采购,库存,入库,出库"
     * 用于Layer 2关键词快速匹配
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 工作流分类(字典值: ai_workflow_category)
     * 取值: 0=业务处理, 1=知识问答, 2=通用对话
     *
     * 注意: 仅用于前端UI筛选,不参与路由逻辑
     */
    @TableField("category")
    private String category;

    /**
     * 优先级 (0-100)
     * 数值越高,路由匹配时越优先
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 最后测试运行时间
     * 用于判断是否可发布 (测试时间 > 更新时间)
     */
    @TableField("last_test_time")
    private LocalDateTime lastTestTime;
}
