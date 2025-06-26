package com.xinyirun.scm.bean.system.bo.user.api;

import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;

public class ApiUserBo extends User implements Serializable {

    private static final long serialVersionUID = 1761069807018016812L;

    @Getter
    private Integer id;

    @Getter
    private SAppConfigEntity sAppConfigEntity;

    /**
     * @param id
     * @param username
     * @param password
     * @param authorities
     */
    public ApiUserBo(Integer id,
                      String username,
                      String password,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    /**
     * @param id
     * @param username
     * @param password
     * @param enabled
     * @param accountNonExpired
     * @param credentialsNonExpired
     * @param accountNonLocked
     * @param authorities
     */
    public ApiUserBo(Integer id,
                      String username,
                      String password,
                      boolean enabled,
                      boolean accountNonExpired,
                      boolean credentialsNonExpired,
                      boolean accountNonLocked,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
        this.id = id;
    }

    public ApiUserBo setUser(SAppConfigEntity sAppConfigEntity) {
        this.sAppConfigEntity = sAppConfigEntity;
        return this;
    }
}
