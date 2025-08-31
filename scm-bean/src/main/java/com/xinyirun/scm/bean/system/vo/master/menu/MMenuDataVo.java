package com.xinyirun.scm.bean.system.vo.master.menu;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 菜单信息
 * </p>
 *
 * @author zxh
 * @since 2020-07-01
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "菜单信息vo", description = "菜单信息vo")
@EqualsAndHashCode(callSuper=false)
public class MMenuDataVo extends TreeNode implements Serializable {

    private static final long serialVersionUID = 2600003769017036105L;

    private Long id;

    /**
     * 默认菜单（0默认 1非默认）
     */
    private Boolean is_default;

    /**
     * 为页面上的顶部导航栏服务，取值为id需要转化为string
     */
    private String index;

    /**
     * 编码
     */
    private String code;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 拼音
     */
    private String name_py;

    /**
     * 根结点id
     */
    private Long root_id;

    private Long [] root_ids;

    /**
     * 父菜单ID
     */
    private Long parent_id;

    /**
     * 儿子个数
     */
    private Integer son_count;

    /**
     * 级联
     */
    private Long value;
    private String label;

    private String depth_name;
    private String depth_id;
    private List<Long> depth_id_array;
    private String parent_depth_id;
    private List<Long> parent_depth_id_array;

//    /**
//     * 排序
//     */
//    private Integer sort;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String type;
    private String type_name;

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

    private String page_info;

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

    private List<MFunctionInfoVo> function_info;


    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


}
