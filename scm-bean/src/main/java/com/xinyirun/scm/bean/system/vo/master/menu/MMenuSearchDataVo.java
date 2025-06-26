package com.xinyirun.scm.bean.system.vo.master.menu;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单信息
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MMenuSearchDataVo extends TreeNode implements Serializable {

    
    private static final long serialVersionUID = 4246609021517662996L;

    private Long id;

    private String index;

    /**
     * 编码
     */
    private String code;


    /**
     * 拼音
     */
    private String name_py;

    /**
     * 首字母拼音
     */
    private String name_first_py;


    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String type;

    /**
     * 请求地址
     */
    private String path;

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
     * 只需要meta_title 和 meta_icon
     */
    private MMenuSearchDataTitleVo meta;

    /**
     * 是否收藏
     */
    private Boolean is_collection;

    /**
     * permision_menu_id
     */
    private Integer permission_menu_id;

}
