package com.xinyirun.scm.bean.entity.master.user.jwt;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2021-12-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_user_jwt_token")
public class MUserJwtTokenEntity implements Serializable {

    private static final long serialVersionUID = -541743453143221882L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long user_id;

    @TableField("staff_id")
    private Long staff_id;

    @TableField("token")
    private String token;

    @TableField("token_expires_at")
    private LocalDateTime token_expires_at;

    @TableField("last_login_date")
    private LocalDateTime last_login_date;

    @TableField("last_logout_date")
    private LocalDateTime last_logout_date;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField("c_id")
    private Long c_id;
}
