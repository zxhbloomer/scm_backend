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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_notice_staff")
public class BNoticeStaffEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -7998517485229287251L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * s_notice 表主键id
     */
    @TableField("notice_id")
    private Integer notice_id;

    /**
     * m_position 表主键id
     */
    @TableField("position_id")
    private Integer position_id;

    /**
     * m_staff 表 主键iid
     */
    @TableField("staff_id")
    private Integer staff_id;

    /**
     * 是否已读
     */
    @TableField("is_read")
    private String is_read;

    /**
     * 已读时间
     */
    @TableField("read_time")
    private LocalDateTime read_time;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;


}
