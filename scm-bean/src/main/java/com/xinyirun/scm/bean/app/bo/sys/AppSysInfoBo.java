package com.xinyirun.scm.bean.app.bo.sys;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppSysInfoBo implements Serializable {
    private static final long serialVersionUID = 6923514063346583166L;
    /** 开发者模式，可以跳过验证码 */
    private Boolean developModel;
}