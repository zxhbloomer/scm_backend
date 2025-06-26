package com.xinyirun.scm.bean.system.vo.business.alarm;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class BAlarmGroupVo implements Serializable {

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
     * 简称
     */
    private String short_name;

    /**
     * 名称拼音
     */
    private String namePinyin;

    /**
     * 简称拼音
     */
    private String short_name_pinyin;

    /**
     * 名称简拼
     */
    private String name_pinyin_abbr;

    /**
     * 简称简拼
     */
    private String short_name_pinyin_abbr;

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
    private Integer c_id;

    private String c_name;

    /**
     * 修改人id
     */
    private Integer u_id;

    private String u_name;

    /**
     * 员工数量
     */
    private int staff_count;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

}
