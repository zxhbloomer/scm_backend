package com.xinyirun.scm.bean.app.bo.jwt.user;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class AppJwtBaseBo implements Serializable {

    private static final long serialVersionUID = 1928489623894572254L;
    /**
     * user_Id
     */
    private String username;
    /**
     * user_Id
     */
    private Long user_Id;

    /**
     * staff_ID
     */
    private Long staff_Id;
    private String staff_code;

    /**
     * 帐号管理员
     */
    private Boolean admin;

    /**
     * 微信unionid
     */
    private String wx_unionid;

    /**
     * token 过期日期
     */
    private LocalDateTime token_expires_at;

    /**
     * token 过期日期
     */
    private LocalDateTime last_login_date;

    /**
     * 帐号所拥有的权限
     */
    private Set<String> operationSet = new HashSet<String>();

    private String extra;

}
