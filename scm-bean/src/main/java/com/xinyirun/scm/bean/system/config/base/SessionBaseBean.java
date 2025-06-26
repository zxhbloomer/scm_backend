package com.xinyirun.scm.bean.system.config.base;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class SessionBaseBean implements Serializable {

    private static final long serialVersionUID = 6503859853529665708L;

    /**
     * 会话ID
     */
    private String session_id;

    /**
     * 帐号ID
     */
    private Long accountId;

    /**
     * staff_ID
     */
    private Long staff_Id;

    /**
     * 帐号是否租户的管理员
     */
    private Boolean tenantAdmin;

    /**
     * 帐号是否租户的管理员
     */
    private Boolean admin;

    /**
     * 帐号所拥有的权限
     */
    private Set<String> operationSet = new HashSet<String>();

    private String extra;

}
