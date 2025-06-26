package com.xinyirun.scm.bean.entity.sys.mail;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/12/12 11:31
 */

@Data
@TableName("s_mail_config")
public class SMailConfigEntity implements Serializable {

    private static final long serialVersionUID = 91012561250876324L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 邮箱服务器host
     */
    @TableField("host")
    private String host;

    /**
     * 邮箱服务器 port
     */
    @TableField("port")
    private Integer port;

    /**
     * 邮箱服务器 username
     */
    @TableField("username")
    private String username;

    /**
     * 邮箱服务器 password
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱服务器 sender
     */
    @TableField("sender")
    private String sender;

    /**
     * 邮箱服务器 from
     */
    @TableField("from_")
    private String from_;

    /**
     * 是否默认配置，0:否。1:是
     */
    @TableField("activity")
    private Boolean activity;

    /**
     * 安全传输方式 1:plain 2:tls 3:ssl
     */
    @TableField("security_type")
    private Integer security_type;

    /**
     * 创建人
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 最后修改人
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 最后修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 0:未删除。1:已删除
     */
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 版本
     */
    @TableField("dbversion")
    @Version
    private Integer dbversion;
}
