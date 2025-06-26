package com.xinyirun.scm.bean.entity.tenant.manager.user;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 租户管理表
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_tenant_manager")
public class STenantManagerEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 3877210330233224651L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 租户code
     */
    @TableField("tenant")
    private String tenant;

    /**
     * 数据库连接URL
     */
    @TableField("url")
    private String url;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String user_name;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 数据库名
     */
    @TableField("database_name")
    private String database_name;

    /**
     * 数据库主机
     */
    @TableField("host")
    private String host;

    /**
     * 状态
     */
    @TableField("status")
    private Boolean status;

    /**
     * 到期时间
     */
    @TableField("expire_date")
    private Date expire_date;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


}
