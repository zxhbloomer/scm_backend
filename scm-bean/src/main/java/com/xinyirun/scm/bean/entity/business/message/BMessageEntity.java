package com.xinyirun.scm.bean.entity.business.message;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * websocket 消息通知表
 *
 * @author xinyirun
 * @since 2023-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_message")
public class BMessageEntity implements Serializable {

    private static final long serialVersionUID = 710880754744872281L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * 类型. 0待办, 1预警, 2通知
     */
    @TableField("type")
    private String type;

    /**
     * 员工id
     */
    @TableField("staff_id")
    private Integer staff_id;

    /**
     * 员工id
     */
    @TableField("alarm_rules_type")
    private String alarm_rules_type;

    /**
     * 1已读, 0未读
     */
    @TableField("status")
    private String status;

    /**
     * 红色标签
     */
    @TableField("label")
    private String label;

    /**
     * 原表格
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 原表格code
     */
    @TableField("serial_code")
    private String serial_code;

    /**
     * 原表格id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField("msg")
    private String msg;

   @TableField("serial_status")
    private String serial_status;

}
