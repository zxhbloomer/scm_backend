package com.xinyirun.scm.bean.system.bo.session.user.rbac;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜单meta数据
 * @ClassName: PermissionBo
 * @Description:
 * @Author: zxh
 * @date: 2019/11/14
 * @Version: 1.0
 */
@Data
// @ApiModel(value = "菜单meta数据", description = "菜单meta数据")
@EqualsAndHashCode(callSuper=false)
public class PermissionMenuMetaBo implements Serializable {

    private static final long serialVersionUID = -1552424179179951751L;

    /**
     * 顶部导航栏对应的编号
     * top_nav_code
     */
    private String top_nav_code;

    /**
     * title
     */
    private String title;

    /**
     * name
     */
    private String name;

    /**
     * icon
     */
    private String icon;

    /**
     * noCache
     */
    private boolean noCache;

    /**
     * affix
     */
    private boolean affix;

    /**
     * 面包屑
     * if set false, the item will hidden in breadcrumb(default is true)
     */
    private boolean breadcrumb;

    /**
     * 高亮菜单
     */
    private String activeMenu;

    /**
     * 高亮topnav的index
     */
    private String active_topnav_index;
    private String active_topnav_code;

    /**
     * page_code
     */
    private String page_code;
}
