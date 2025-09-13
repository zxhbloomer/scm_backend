package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.io.Serial;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_users")
public class BpmUsersEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -3356383847594165025L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户编号
     */
    @TableField("user_code")
    private String user_code;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String user_name;

    /**
     * 拼音  全拼
     */
    @TableField("pingyin")
    private String pingyin;


    /**
     * 昵称
     */
    @TableField("alisa")
    private String alisa;

    /**
     * 头像base64
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别
     */
    @TableField("sex")
    private Boolean sex;

    /**
     * 入职日期
     */
    @TableField("entry_date")
    private LocalDate entry_date;

    /**
     * 离职日期
     */
    @TableField("leave_date")
    private LocalDate leave_date;

    /**
     * 管理级别 0=主管理员 1=子管理员 2=普通员工
     */
    @TableField("admin")
    private Integer admin;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @TableField("u_time")
    private LocalDateTime u_time;

    /**
     * 租户编号
     */
    @TableField("tenant_code")
    private byte[] tenant_code;

    /**
     * 关联m_staff主键
     */
    @TableField("staff_id")
    private Long staff_id;

    /**
     * 关联m_user主键
     */
    @TableField("user_id")
    private Long user_id;

    /**
     * 逻辑删除标识
     */
    @TableField("is_del")
    private Boolean is_del;

}
