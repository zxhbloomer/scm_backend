package com.xinyirun.scm.bean.app.bo.user.login;

import com.xinyirun.scm.bean.app.vo.master.user.AppMUserVo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;

public class AppMUserBo extends User implements Serializable {
    private static final long serialVersionUID = 1224893807773362361L;
    @Getter
    private Long id;

    @Getter
    private AppMUserVo app_user;

    /**
     * @param id
     * @param username
     * @param password
     * @param authorities
     */
    public AppMUserBo(Long id,
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
    public AppMUserBo(Long id,
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

    public AppMUserBo setUser(AppMUserVo user) {
        this.app_user = user;
        return this;
    }
}
