package com.xinyirun.scm.bean.system.vo;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 短信验证码
 * </p>
 *
 * @author zxh
 * @since 2019-12-09
 */
@Data
// @ApiModel(value = "短信验证码", description = "短信验证码")
@EqualsAndHashCode(callSuper=false)
public class SSmsCodeVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3343310432986309357L;

    private String mobile;

    private String sms_code;

}
