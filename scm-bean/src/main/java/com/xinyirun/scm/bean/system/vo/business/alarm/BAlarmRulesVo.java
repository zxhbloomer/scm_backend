package com.xinyirun.scm.bean.system.vo.business.alarm;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class BAlarmRulesVo implements Serializable {

    private static final long serialVersionUID = -6038762455816378777L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 预警规则名称名称,, 手填, 不能重复
     */
    private String name;

    /**
     * 阈值类型, 事件预警(同步失败), 阈值预警 (0 事件预警, 1阈值预警)
     */
    private String type;
    private String type_name;

    /**
     * 预警方式, 0单次预警 (每天预警一次), 1周期预警 (每间隔xx分钟进行预警判断, 需设置预警间隔)
     */
    private String notice_plan;

    /**
     * 预警发送间隔, 单位: 分钟
     */
    private Integer notice_time;

    /**
     * 预警通知方式, 0消息通知, 1弹出显示
     */
    private String notice_type;

    private String notice_type_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本
     */
    private Integer dbversion;

    /**
     * 预警规则, 名称
     */
    private String rule_type
            , rule_type_name;

    /**
     * 定时任务名称
     */
    private String job_name;

    /**
     * 定时任务 ID
     */
    private Integer job_id;

    /**
     * 创建人, 更新人
     */
    private String c_name
            , u_name;

    private PageCondition pageCondition;

    /**
     * 是否启用
     */
    private String is_using;

    /**
     * 预警组
     */
    private List<JSONObject> group_list;

    /**
     * 预警人员
     */
    private List<JSONObject> staff_list;

    /**
     * 预警组
     */
    private String group_name;

}
