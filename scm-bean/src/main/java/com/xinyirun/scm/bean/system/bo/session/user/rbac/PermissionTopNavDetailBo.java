package com.xinyirun.scm.bean.system.bo.session.user.rbac;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 顶部导航栏数据
 * @ClassName: PermissionTopNavBo
 * @Description:
 * @Author: zxh
 * @date: 2019/11/14
 * @Version: 1.0
 */
@Data
// @ApiModel(value = "顶部导航栏数据", description = "顶部导航栏数据")
@EqualsAndHashCode(callSuper=false)
public class PermissionTopNavDetailBo extends TreeNode implements Serializable {

    private static final long serialVersionUID = -2154124049072701204L;

    private Long id;

    /**
     * 顶部导航栏的rownum_char，index
     */
    private String index;
    private String sort_index;

    /**
     * 菜单类型
     * T:顶部导航栏
     * R：根节点
     * P：页面
     */
    private String type;

    private String active_code;

    /**
     * 菜单meta数据
     */
    private PermissionMenuMetaBo meta;


}
