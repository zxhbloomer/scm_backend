package com.xinyirun.scm.bean.entity.bpm;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审批流待办已办
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_todo")
public class BpmTodoEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -2214948117998641416L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 审批编号(根据process_instance_id搜索bpm_instance，获取process_code)
     */
    @TableField("process_code")
    private String process_code;

    /**
     * 任务 ID，对应 Flowable 中的节点 ID
     */
    @TableField("node_id")
    private String node_id;

    /**
     * 任务 ID，对应 Flowable 中的任务 ID
     */
    @TableField("task_id")
    private String task_id;

    /**
     * 流程实例 ID
     */
    @TableField("process_instance_id")
    private String process_instance_id;

    /**
     * 流程定义 ID
     */
    @TableField("process_definition_id")
    private String process_definition_id;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String task_name;

    /**
     * 任务处理人 code
     */
    @TableField("assignee_code")
    private String assignee_code;

    /**
     * 任务处理人姓名
     */
    @TableField("assignee_name")
    private String assignee_name;

    /**
     * bpm_process_templates.form_items
     */
    @TableField("form_items")
    private String form_items;

    /**
     * bpm_process_templates.process
     */
    @TableField("process")
    private String process;

    /**
     * 业务键，可用于关联业务数据
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 表名
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 任务到期时间，可选
     */
    @TableField("due_date")
    private LocalDateTime due_date;

    /**
     * 0-代办 1-已办
     */
    @TableField("status")
    private String status;

    /**
     * 审批时间
     */
    @TableField("approve_time")
    private LocalDateTime approve_time;

    /**
     * 审批类型  0=处理中 1=同意 2=委派 3=委派人完成 4=拒绝 5=转办 6=退回 7=加密 8=查到签上的人 9=减签 10=评论
     */
    @TableField("approve_type")
    private String approve_type;

    /**
     * 备注（审批意见）
     */
    @TableField("remark")
    private String remark;

    /**
     * 上一节点id
     */
    @TableField("last_todo_id")
    private Integer last_todo_id;

    /**
     * 更新时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 任务创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

}
