package com.xinyirun.scm.bean.entity.tenant.manager.user;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户登录系统表（用户名密码），与各个租户系统联动，实时更新
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_login_user")
public class SLoginUserEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 387137971657108528L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Integer tenant_id;

    /**
     * 租户code
     */
    @TableField("tenant_code")
    private String tenant_code;

    /**
     * 登录模式：（10：手机号码；20：邮箱）
     */
    @TableField("login_type")
    private String login_type;

    /**
     * 登陆用户名
     */
    @TableField("login_name")
    private String login_name;

    /**
     * 系统用户=10,职员=20,客户=30,供应商=40,其他=50,认证管理员=60,审计管理员=70
     */
    @TableField("type")
    private String type;

    /**
     * 自我介绍
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 密码
     */
    @TableField("pwd")
    private String pwd;

    /**
     * 是否是已经删除
     */
    @TableField("is_del")
    private Boolean is_del;

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
     * 最后登陆时间
     */
    @TableField("last_login_date")
    private LocalDateTime last_login_date;

    /**
     * 最后主动登出时间
     */
    @TableField("last_logout_date")
    private LocalDateTime last_logout_date;

    @TableField("c_id")
    private Long cId;

    @TableField("c_time")
    private LocalDateTime cTime;

    @TableField("u_id")
    private Long uId;

    @TableField("u_time")
    private LocalDateTime uTime;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 密码最后修改时间
     */
    @TableField("pwd_u_time")
    private LocalDateTime pwd_u_time;

    /**
     * 微信openid
     */
    @TableField("wx_openid")
    private String wx_openid;

    /**
     * 微信accesstoken
     */
    @TableField("wx_access_token")
    private String wx_access_token;

    /**
     * 微信refreshtoken
     */
    @TableField("wx_refresh_token")
    private String wx_refresh_token;

    /**
     * 微信expiresIn
     */
    @TableField("wx_expires_in")
    private Integer wx_expires_in;

    /**
     * 微信unionid
     */
    @TableField("wx_unionid")
    private String wx_unionid;
}
