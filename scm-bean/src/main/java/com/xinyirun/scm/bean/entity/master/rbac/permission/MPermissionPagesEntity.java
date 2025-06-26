package com.xinyirun.scm.bean.entity.master.rbac.permission;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限页面表
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_permission_pages")
public class MPermissionPagesEntity implements Serializable {

    private static final long serialVersionUID = 2168133794102374994L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限id
     */
    @TableField("permission_id")
    private Long permission_id;

    /**
     * 权限菜单id
     */
    @TableField("permission_menu_id")
    private Long permission_menu_id;

    /**
     * page_id，数据通过复制s_pages，为不破坏表连接关系，该字段记录s_pages.id
     */
    @TableField("page_id")
    private Long page_id;

    /**
     * 是否启用(0:false-已禁用,1:true-已启用)
     */
    @TableField("is_enable")
    private Boolean is_enable;

    /**
     * 配置vue export default  name时所使用的type：constants_program.P_VUE_SETTING
     */
    @TableField("code")
    private String code;

    /**
     * 页面名称
     */
    @TableField("name")
    private String name;

    /**
     * 模块地址：@/views/10_system/vuesetting/vue
     */
    @TableField("component")
    private String component;

    /**
     * 权限标识
     */
    @TableField("perms")
    private String perms;

    /**
     * 页面的名称
     */
    @TableField("meta_title")
    private String meta_title;

    /**
     * 菜单中显示的icon
     */
    @TableField("meta_icon")
    private String meta_icon;

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
