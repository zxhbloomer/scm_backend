package com.xinyirun.scm.bean.system.vo.business.sync;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 同步状态
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSyncStatusVo implements Serializable {

    private static final long serialVersionUID = -6139933429295211047L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 关联id
     */
    private Integer serial_id;

    private Integer serial_detail_id;


    /**
     * 入/出库单id
     */
    private Integer bill_id;

    /**
     * 入/出库单号
     */
    private String bill_code;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 关联单号类型名称
     */
    private String serial_type_name;

    /**
     * 关联单号
     */
    private String serial_code;

    private String serial_detail_code;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String status_name;


    /**
     * 同步失败信息
     */
    private String msg;

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
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 同步开始时间
     */
    private String c_time_start;

    /**
     * 同步结束时间
     */
    private String c_time_end;
}
