package com.xinyirun.scm.bean.app.vo.master.user;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
// @Schema( name = "app员工主表", description = "app员工主表")
@EqualsAndHashCode(callSuper=false)
public class AppMStaffVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 4666240607082330513L;


    /**
     * 员工id
     */
    private Long staff_id;
    private String code;

    /**
     * 姓名
     */
    private String staff_name;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 登录用户id，关联id
     */
    private Long user_id;

    /**
     * 手机号码
     */
    private String user_login_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
