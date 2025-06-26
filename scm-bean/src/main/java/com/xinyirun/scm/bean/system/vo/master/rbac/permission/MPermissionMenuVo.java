package com.xinyirun.scm.bean.system.vo.master.rbac.permission;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限菜单信息
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限菜单信息", description = "权限菜单信息")
@EqualsAndHashCode(callSuper=false)
public class MPermissionMenuVo implements Serializable {

    private static final long serialVersionUID = -6910039744510003538L;

    private Long id;

    /**
     * 权限id
     */
    private Long permission_id;

    /**
     * menu_id，数据通过复制m_menu，为不破坏表连接关系，该字段记录m_menu.id
     */
    private Long menu_id;

    /**
     * 是否启用(0:false-已禁用,1:true-已启用)
     */
    private Boolean is_enable;

    /**
     * 默认菜单（0非默认 1默认）
     */
    private Boolean is_default;

    /**
     * 编码
     */
    private String code;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 根结点id
     */
    private Long root_id;

    /**
     * 父菜单ID
     */
    private Long parent_id;

    /**
     * 儿子个数
     */
    private Integer son_count;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 菜单类型（R：根节点；N：结点；P：页面）
     */
    private String type;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private Boolean visible;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 页面id
     */
    private Long page_id;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * 请求地址
     */
    private String path;

    /**
     * 路由名，需要唯一，很重要，且需要vue这里手工录入
     */
    private String route_name;

    /**
     * 菜单名
     */
    private String meta_title;

    /**
     * 菜单icon
     */
    private String meta_icon;

    /**
     * 模块
     */
    private String component;

    /**
     * 附在导航栏不可关闭
     */
    private Boolean affix;

    /**
     * 说明
     */
    private String descr;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
