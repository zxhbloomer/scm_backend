package com.xinyirun.scm.bean.entity.master.user;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户主表
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_user")
public class MUserEntity extends BaseEntity<MUserEntity> implements Serializable {

    private static final long serialVersionUID = -5981357904929165446L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录模式：（10：手机号码；20：邮箱）
     */
    @TableField("login_type")
    private String login_type;


    /**
     * 登录用户名
     */
    @TableField("login_name")
    private String login_name;

    /**
     * 系统用户=10,职员=20,客户=30,供应商=40,其他=50,认证管理员=60,审计管理员=70
     */
    @TableField("type")
    private String type;

    /**
     * 说明
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 密码

     */
    @TableField("pwd")
    private String pwd;

    /**
     * 是否锁定

     */
    @TableField("is_lock")
    private Boolean is_lock;

    /**
     * 是否启用(1:true-已启用,0:false-已禁用)
     */
    @TableField("is_enable")
    private Boolean is_enable;

    /**
     * 生效时间

     */
    @TableField("effective_date")
    private LocalDateTime effective_date;

    /**
     * 失效时间

     */
    @TableField("invalidation_date")
    private LocalDateTime invalidation_date;

    /**
     * 登录错误次数

     */
    @TableField("err_count")
    private Integer err_count;

    /**
     * 所属用户组
     用户组织范围
     包含下级组织的组织范围

     */
    @TableField("group_id")
    private String group_id;

    @TableField("staff_id")
    private Long staff_id;

    /**
     * 用户锁定时间
     */
    @TableField("locked_time")
    private LocalDateTime locked_time;

    /**
     * 是否为业务管理员
     */
    @TableField("is_biz_admin")
    private Boolean is_biz_admin;

    /**
     * 是否修改过密码
     */
    @TableField("is_changed_pwd")
    private Boolean is_changed_pwd;

    /**
     * 传统认证方式=0,智能钥匙认证=1,动态密码锁=2,指纹认证方式=3
     */
    @TableField("login_author_way")
    private String login_author_way;

    /**
     * 历史密码
     */
    @TableField("pwd_his_pwd")
    private String pwd_his_pwd;

    /**
     * 头像路径
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 最后登录时间
     */
    @TableField("last_login_date")
    private LocalDateTime last_login_date;

    /**
     * 租户id
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    /**
     * 是否删除
     */
    @TableField(value = "is_del", fill = FieldFill.INSERT)
    private Boolean is_del;

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
     * 密码最后修改时间
     */
    @TableField(value="pwd_u_time")
    private LocalDateTime pwd_u_time;

    /**
     * 微信openid
     */
    @TableField(value="wx_openid")
    private String wx_openid;

    /**
     * 微信accesstoken
     */
    @TableField(value="wx_access_token")
    private String wx_access_token;

    /**
     * 微信refreshtoken
     */
    @TableField(value="wx_refresh_token")
    private String wx_refresh_token;

    /**
     * 微信expiresIn
     */
    @TableField(value="wx_expires_in")
    private Integer wx_expires_in;

    /**
     * 微信unionid
     */
    @TableField(value="wx_unionid")
    private String wx_unionid;
}
