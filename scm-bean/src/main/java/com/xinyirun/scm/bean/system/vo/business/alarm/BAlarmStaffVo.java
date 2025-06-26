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
 * 预警组
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BAlarmStaffVo implements Serializable {

    private static final long serialVersionUID = -2182430287822485582L;

    /**
     * id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;

    /**
     * 登录名称
     */
    private String login_name;

    /**
     * 员工ID
     */
    private Integer staff_id;

    /**
     * 组 ID
     */
    private Integer group_id;

    /**
     * 员工姓名
     */
    private String staff_name;

    private LocalDateTime u_time
            , c_time;

    private Integer u_id
            , c_id;

    private String u_name
            , c_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * 所属组集合
     */
    private List<JSONObject> group_name_list;

    /**
     * 预警组名称
     */
    private String group_name;

}
