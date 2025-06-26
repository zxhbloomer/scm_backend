package com.xinyirun.scm.bean.system.bo.session.user.rbac;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜单数据
 * @ClassName: PermissionBo
 * @Description:
 * @Author: zxh
 * @date: 2019/11/14
 * @Version: 1.0
 */
@Data
// @ApiModel(value = "菜单数据", description = "菜单数据")
@EqualsAndHashCode(callSuper=false)
public class PermissionMenuBo extends TreeNode implements Serializable {

    private static final long serialVersionUID = 2876122411592935277L;

    private String code;
    private Long id;

    private Long menu_id;

    /**
     * 父节点ID
     */
    private Long parent_id;

    /**
     * 根节点ID
     */
    private Long root_id;

    /**
     * 页面id，如有，否null
     */
    private Long page_id;

    /**
     * 顶部导航栏的code
     */
    private String nav_code;

    /**
     * 顶部导航栏的rownum_char，index
     */
    private String index;

    /**
     * 菜单类型
     * T:顶部导航栏
     * R：根节点
     * P：页面
     */
    private String type;

    /**
     * noRedirect:不跳转
     * 其他：正常跳转
     */
    private String redirect;

    /**
     * page_code
     */
    private String page_code;

    /**
     * path
     */
    private String path;

    /**
     * route_name
     */
    private String route_name;

    /**
     * page页面的地址
     */
    private String component;

    /**
     * 是否启用(0:false-已禁用,1:true-已启用)
     */
    private Boolean is_enable;


    /**
     * 菜单meta数据
     */
    private PermissionMenuMetaBo meta;


}
