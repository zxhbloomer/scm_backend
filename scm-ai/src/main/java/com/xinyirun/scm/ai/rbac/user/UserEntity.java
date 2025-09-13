package com.xinyirun.scm.ai.rbac.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@Accessors(chain = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("scm_ai_user")
public class UserEntity extends BytedeskBaseEntity {

    private static final long serialVersionUID = 1L;

    private String num;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String country = "86";
    private String mobile;
    private String avatar;
    private String description;
    private String type;
    private Boolean emailVerified = false;
    private Boolean mobileVerified = false;
    private Boolean enabled = true;
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
}