package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SsoUserInfoVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 78538947465525900L;

    /**
     * MD5加密后的用户名
     */
    private String md5_user_name;

    /**
     * 跳转页面
     */
    private String des_page;

    /**
     * 参数名
     */
    private String params_name;

    /**
     * 参数值
     */
    private String params_value;

    /**
     * 用户名
     */
    private String user_name;

    /**
     * 跳转链接
     */
    private String login_url;

}
