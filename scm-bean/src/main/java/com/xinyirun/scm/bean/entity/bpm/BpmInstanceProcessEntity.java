package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审批流程节点
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_instance_process")
public class BpmInstanceProcessEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -9192973018653564566L;

    /**
     * id（主键，自增）
     */
    @TableId("id")
    private Integer id;

    /**
     * 节点id
     */
    @TableField("node_id")
    private String node_id;

    /**
     * 审批编号
     */
    @TableField("process_code")
    private String process_code;

    /**
     * 下个要执行的节点
     */
    @TableField("is_next")
    private String is_next;

    /**
     * 任务 ID，对应 Flowable 中的任务 ID
     */
    @TableField("task_id")
    private String task_id;

    /**
     * 审批类型 AND=会签  OR=或签  NEXT=顺序会签
     */
    @TableField("approval_mode")
    private String approval_mode;

    /**
     * 节点类型 ROOT=发起人  APPROVAL=审批节点  CC=抄送   COMMENT=评论     CANCEL=撤销  TASK=办理
     */
    @TableField("node_type")
    private String node_type;

    /**
     * 节点名称 = 审批人,发起人,办理人,抄送人
     */
    @TableField("name")
    private String name;

    /**
     * 发起人编号
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 发起人姓名
     */
    @TableField("owner_name")
    private String owner_name;

    /**
     * 节点操作，点击的按钮 agree,refuse,comment,beforeAdd,afterAdd,transfer,cancel,recall
     */
    @TableField("action")
    private String action;

    /**
     * 评论id
     */
    @TableField("comment_id")
    private Integer comment_id;

    /**
     * 执行情况 null-等待执行 running-进行中，complete-完成 ，pass-审核通过，cancel-取消，refuse-拒绝
     */
    @TableField("result")
    private String result;


    /**
     * 开始审批时间
     */
    @TableField("start_time")
    private LocalDateTime start_time;

    /**
     * 完成审批时间
     */
    @TableField("finish_time")
    private LocalDateTime finish_time;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;


}
