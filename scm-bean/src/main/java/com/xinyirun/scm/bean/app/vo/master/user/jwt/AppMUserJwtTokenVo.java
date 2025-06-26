package com.xinyirun.scm.bean.app.vo.master.user.jwt;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * jwt 令牌vo
 * </p>
 *
 * @author zhangxiaohua
 * @since 2021-12-18
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "jwt 令牌vo", description = "jwt 令牌vo")
@EqualsAndHashCode(callSuper=false)
public class AppMUserJwtTokenVo extends BaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 1913512757142822127L;

    private Long id;

    private Long user_id;

    private Long staff_id;
    private String staff_code;

    private String token;

    private LocalDateTime token_expires_at;

    private LocalDateTime last_login_date;

    private LocalDateTime c_time;

    private Long c_id;

    private Boolean staff_del;
    private Boolean user_del;
    private Boolean is_lock;
    private Boolean is_enable;

}
