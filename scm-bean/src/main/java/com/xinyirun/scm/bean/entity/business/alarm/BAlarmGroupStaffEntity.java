package com.xinyirun.scm.bean.entity.business.alarm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_alarm_group_staff")
@NoArgsConstructor
@AllArgsConstructor
public class BAlarmGroupStaffEntity implements Serializable {

    private static final long serialVersionUID = 7833688763954767015L;

    @TableId("id")
    private Integer id;

    @TableField("staff_id")
    private Integer staff_id;

    @TableField("alarm_group_id")
    private Integer alarm_group_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    public BAlarmGroupStaffEntity(Integer staff_id, Integer alarm_group_id) {
        this.staff_id = staff_id;
        this.alarm_group_id = alarm_group_id;
    }
}
