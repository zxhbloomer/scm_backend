package com.xinyirun.scm.bean.entity.busniess.alarm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 预警规则清单
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_alarm_rules")
public class BAlarmRulesEntity implements Serializable {

    private static final long serialVersionUID = -8429622358588619362L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 预警规则名称名称,, 手填, 不能重复
     */
    @TableField("name")
    private String name;

    /**
     * 阈值类型, 事件预警(同步失败), 阈值预警 (0 事件预警, 1阈值预警)
     */
    @TableField("type")
    private String type;

    /**
     * 预警方式, 0单次预警 (每天预警一次), 1周期预警 (每间隔xx分钟进行预警判断, 需设置预警间隔)
     */
    @TableField("notice_plan")
    private String notice_plan;

    /**
     * 预警发送间隔, 单位: 分钟
     */
    @TableField("notice_time")
    private Integer notice_time;

    /**
     * 预警通知方式, 0消息通知, 1弹出显示
     */
    @TableField("notice_type")
    private String notice_type;

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

    /**
     * 数据版本
     */
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 是否启用, 0否, 1启用
     */
    @TableField("is_using")
    private String is_using;

    /**
     * 预警规则 1 实时预警, 2定时任务
     */
    @TableField("rule_type")
    private String rule_type;

    /**
     * 定时任务 id
     */
    @TableField("job_id")
    private Integer job_id;


}
