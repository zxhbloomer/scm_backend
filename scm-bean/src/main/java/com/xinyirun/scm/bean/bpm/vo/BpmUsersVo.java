package com.xinyirun.scm.bean.bpm.vo;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * BPM用户表 VO类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BpmUsersVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 用户编号
     */
    private String user_code;

    /**
     * 用户名
     */
    private String user_name;

    /**
     * 拼音 全拼
     */
    private String pingyin;

    /**
     * 昵称
     */
    private String alisa;

    /**
     * 头像base64
     */
    private String avatar;

    /**
     * 性别
     */
    private Boolean sex;

    /**
     * 入职日期
     */
    private LocalDate entry_date;

    /**
     * 离职日期
     */
    private LocalDate leave_date;

    /**
     * 管理级别 0=主管理员 1=子管理员 2=普通员工
     */
    private Integer admin;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 租户编号
     */
    private byte[] tenant_code;

    /**
     * 关联m_staff主键
     */
    private Long staff_id;

    /**
     * 关联m_user主键
     */
    private Long user_id;

    /**
     * 逻辑删除标识
     */
    private Boolean is_del;

}