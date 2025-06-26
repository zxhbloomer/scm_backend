package com.xinyirun.scm.bean.entity.master.rbac.permission;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限页面操作表
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_permission_operation")
public class MPermissionOperationEntity implements Serializable {

    private static final long serialVersionUID = 4858890941099495587L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限id
     */
    @TableField("permission_id")
    private Long permission_id;

    /**
     * operation_id，数据通过复制s_pages_function，为不破坏表连接关系，该字段记录s_pages_function.id
     */
    @TableField("operation_id")
    private Long operation_id;

    /**
     * 权限页面id
     */
    @TableField("permission_page_id")
    private Integer permission_page_id;

    /**
     * 页面id
     */
    @TableField("page_id")
    private Long page_id;

    /**
     * 是否启用(0:false-已禁用,1:true-已启用)
     */
    @TableField("is_enable")
    private Boolean is_enable;

    /**
     * 类型：PAGE：主页面上，TABLE：表格上，POPUP：弹出框上
     */
    @TableField("type")
    private String type;

    /**
     * 按钮id
     */
    @TableField("function_id")
    private Long function_id;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 权限标识
     */
    @TableField("perms")
    private String perms;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 租户id
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;
}
