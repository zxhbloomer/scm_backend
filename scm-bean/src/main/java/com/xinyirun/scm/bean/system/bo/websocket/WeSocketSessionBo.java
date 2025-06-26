package com.xinyirun.scm.bean.system.bo.websocket;

import com.xinyirun.scm.bean.system.config.base.SessionBaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.security.Principal;

@Data
@EqualsAndHashCode(callSuper=false)
public class WeSocketSessionBo extends SessionBaseBean implements Serializable, Principal {
    private static final long serialVersionUID = 6820057099713838424L;

    @Override
    public String getName() {
        return super.getStaff_Id().toString();
    }
}
