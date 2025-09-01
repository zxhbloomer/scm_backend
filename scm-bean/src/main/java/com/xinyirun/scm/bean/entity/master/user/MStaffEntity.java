package com.xinyirun.scm.bean.entity.master.user;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_staff")
public class MStaffEntity extends BaseEntity<MStaffEntity> implements Serializable {

    private static final long serialVersionUID = -3272074238285802155L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 全称拼音
     */
    @TableField("name_py")
    private String name_py;


    /**
     * 登录用户id，关联id
     */
    @TableField("user_id")
    private Long user_id;

    /**
     * 男=1,女=2
     */
    @TableField("sex")
    private String sex;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 审核状态:未审核=0,已审核=1
     */
    @TableField("state")
    private Boolean state;

    /**
     * 家庭电话
     */
    @TableField("home_phone")
    private String home_phone;

    /**
     * 办公室电话
     */
    @TableField("office_phone")
    private String office_phone;

    /**
     * 手机号码
     */
    @TableField("mobile_phone")
    private String mobile_phone;

    /**
     * 备用手机号码
     */
    @TableField("mobile_phone_backup")
    private String mobile_phone_backup;

    /**
     * 备用电子邮件
     */
    @TableField("email_backup")
    private String email_backup;

    /**
     * 身份证号码
     */
    @TableField("id_card")
    private String id_card;

    /**
     * 护照号码
     */
    @TableField("passport")
    private String passport;

    /**
     * 是否在职：在职=1,不在职=0,离职=2,离退休=3,返聘=4
     */
    @TableField("service")
    private String service;

    /**
     * 婚否
     */
    @TableField("is_wed")
    private String is_wed;

    /**
     * 名族
     */
    @TableField("nation")
    private String nation;

    /**
     * 学历
     */
    @TableField("degree")
    private String degree;

    /**
     * 所属公司
     */
    @TableField("company_id")
    private Long company_id;

    /**
     * 默认部门
     */
    @TableField("dept_id")
    private Long dept_id;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean is_del;

    /**
     * 是否管理员(true-是,false-否)
     */
    @TableField("is_admin")
    private Boolean is_admin;

    /**
     * 租户id
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


    /**
     * 身份证正面照片id
     */
    @TableField("one_file")
    private Integer one_file;


    /**
     * 身份证背面附件id
     */
    @TableField("two_file")
    private Integer two_file;

}
