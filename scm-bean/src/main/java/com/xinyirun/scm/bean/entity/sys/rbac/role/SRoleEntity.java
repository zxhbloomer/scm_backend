package com.xinyirun.scm.bean.entity.sys.rbac.role;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("s_role")
public class SRoleEntity extends BaseEntity<SRoleEntity> implements Serializable {

    private static final long serialVersionUID = -4476852388271557947L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色类型
     */
    @TableField("type")
    private String type;

    /**
     * 角色编码
     */
    @TableField("code")
    private String code;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 简称
     */
    @TableField("simple_name")
    private String simple_name;

    /**
     * 是否是已经删除(1:true-已删除,0:false-未删除)
     * 
     */
    @TableField(value = "is_del", fill = FieldFill.INSERT)
    private Boolean is_del;


    /**
     * 租户代码
     */
    @TableField("corp_code")
    private String corp_code;

    /**
     * 租户名称
     */
    @TableField("corp_name")
    private String corp_name;

    @TableField(value = "c_id", fill = FieldFill.INSERT)
    private Long c_id;

    @TableField(value = "c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value = "dbversion", fill = FieldFill.INSERT_UPDATE)
    private Integer dbversion;
}
