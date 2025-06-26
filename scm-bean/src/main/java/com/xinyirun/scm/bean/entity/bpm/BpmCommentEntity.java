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
 * 审批流评论
 * </p>
 *
 * @author xinyirun
 * @since 2024-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_comment")
public class BpmCommentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1751595034092712002L;

    @TableId("id")
    private Integer id;

    /**
     * 评论内容
     */
    @TableField("text")
    private String text;

    /**
     * 附件数组
     */
    @TableField("files_id")
    private Integer files_id;

    /**
     * 图片数组
     */
    @TableField("images")
    private String images;

    /**
     * task_id（任务 ID，对应 Flowable 中的任务 ID）
     */
    @TableField("task_id")
    private String task_id;

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
     * 留言方式
     * OPINION_COMMENT	意见评论的标识，可能用于在任务或流程中记录用户的评论意见。
     * OPTION_COMMENT	选项评论的标识，可能用于记录用户选择的选项。
     * COMMENTS_COMMENT	评论的标识，可能用于收集用户在流程中的各种评论信息。
     */
    @TableField("type")
    private String type;

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
