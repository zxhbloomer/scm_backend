package com.xinyirun.scm.bean.system.bo.sys;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SysInfoBo
 * @Description: 系统的关键参数
 * @Author: zxh
 * @date: 2019/12/5
 * @Version: 1.0
 */
@Data
public class SysInfoBo implements Serializable {

    private static final long serialVersionUID = -845176648840726320L;

    /** 开发者模式，可以跳过验证码 */
    private Boolean developModel;
}
