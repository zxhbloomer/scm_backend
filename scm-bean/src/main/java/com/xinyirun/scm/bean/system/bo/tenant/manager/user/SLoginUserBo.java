package com.xinyirun.scm.bean.system.bo.tenant.manager.user;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户登录系统表（用户名密码），与各个租户系统联动，实时更新
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SLoginUserBo implements Serializable {


    @Serial
    private static final long serialVersionUID = -197224200175500759L;

    private Long id;

    /**
     * 租户id
     */
    private Integer tenant_id;

    /**
     * 租户code
     */
    private String tenant_code;

    /**
     * 登录模式：（10：手机号码；20：邮箱）
     */
    private String login_type;

    /**
     * 登陆用户名
     */
    private String login_name;

    /**
     * 系统用户=10,职员=20,客户=30,供应商=40,其他=50,认证管理员=60,审计管理员=70
     */
    private String type;

    /**
     * 自我介绍
     */
    private String introduction;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 是否是已经删除
     */
    private Boolean is_del;

    /**
     * 是否锁定
     */
    private Boolean is_lock;

    /**
     * 是否启用(1:true-已启用,0:false-已禁用)
     */
    private Boolean is_enable;

    /**
     * 生效时间
     */
    private LocalDateTime effective_date;

    /**
     * 失效时间
     */
    private LocalDateTime invalidation_date;

    /**
     * 登录错误次数
     */
    private Integer err_count;

    /**
     * 所属用户组
     用户组织范围
     包含下级组织的组织范围
     */
    private String group_id;

    private Long staff_id;

    /**
     * 用户锁定时间
     */
    private LocalDateTime locked_time;

    /**
     * 是否为业务管理员
     */
    private Boolean is_biz_admin;

    /**
     * 是否修改过密码
     */
    private Boolean is_changed_pwd;

    /**
     * 传统认证方式=0,智能钥匙认证=1,动态密码锁=2,指纹认证方式=3
     */
    private String login_author_way;

    /**
     * 历史密码
     */
    private String pwd_his_pwd;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 最后登陆时间
     */
    private LocalDateTime last_login_date;

    /**
     * 最后主动登出时间
     */
    private LocalDateTime last_logout_date;

    private Long cId;

    private LocalDateTime cTime;

    private Long uId;

    private LocalDateTime uTime;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 密码最后修改时间
     */
    private LocalDateTime pwd_u_time;

    /**
     * 微信openid
     */
    private String wx_openid;

    /**
     * 微信accesstoken
     */
    private String wx_access_token;

    /**
     * 微信refreshtoken
     */
    private String wx_refresh_token;

    /**
     * 微信expiresIn
     */
    private Integer wx_expires_in;

    /**
     * 微信unionid
     */
    private String wx_unionid;
}
