package com.xinyirun.scm.bean.entity.busniess.notice;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 通知表
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_notice")
public class BNoticeEntity implements Serializable {

    
    private static final long serialVersionUID = -2720668280603343695L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 通知类型, 0-系统通知；1-用户通知
     */
    @TableField("type")
    private String type;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 通知详情
     */
    @TableField("msg")
    private String msg;

    /**
     * 通知详情html
     */
    @TableField("html")
    private String html;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 创建人
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT)
    private Long c_id;

    /**
     * 文件地址
     */
    @TableField("file_one")
    private Integer file_one;

    /**
     * 通知状态
     */
    @TableField("status")
    private String status;


}
