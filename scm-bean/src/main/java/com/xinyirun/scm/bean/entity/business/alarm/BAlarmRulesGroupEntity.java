package com.xinyirun.scm.bean.entity.business.alarm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_alarm_rules_group")
public class BAlarmRulesGroupEntity implements Serializable {

    private static final long serialVersionUID = 6099204386745772491L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 预警联系类型, 1人员, 2,预警组
     */
    @TableField("type")
    private String type;

    /**
     * 预警规则id
     */
    @TableField("alarm_id")
    private Integer alarm_id;

    /**
     * m_staff表id
     */
    @TableField("staff_id")
    private Integer staff_id;

    /**
     * 预警组id/预警人员id
     */
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


}
