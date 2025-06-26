package com.xinyirun.scm.bean.entity.master.menu;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜单信息
 * </p>
 *
 * @author zxh
 * @since 2020-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_menu")
public class MMenuEntity implements Serializable {

    private static final long serialVersionUID = 7394845218382257643L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 默认菜单（0默认 1非默认）
     */
    @TableField("is_default")
    private Boolean is_default;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

    /**
     * 菜单名称
     */
    @TableField("name")
    private String name;

    /**
     * 根结点id
     */
    @TableField("root_id")
    private Long root_id;

    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    private Long parent_id;

    /**
     * 儿子个数
     */
    @TableField("son_count")
    private Integer son_count;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @TableField("type")
    private String type;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @TableField("visible")
    private Boolean visible;

    /**
     * 权限标识
     */
    @TableField("perms")
    private String perms;

    /**
     * 模块id
     */
    @TableField("page_id")
    private Long page_id;

    /**
     * 页面code
     */
    @TableField("page_code")
    private String page_code;

    /**
     * 请求地址
     */
    @TableField("path")
    private String path;

    /**
     * 路由名，需要唯一，很重要，且需要vue这里手工录入
     */
    @TableField("route_name")
    private String route_name;

    /**
     * 菜单名
     */
    @TableField("meta_title")
    private String meta_title;

    /**
     * 菜单icon
     */
    @TableField("meta_icon")
    private String meta_icon;

    /**
     * 模块
     */
    @TableField("component")
    private String component;

    /**
     * 附在导航栏不可关闭
     */
    @TableField("affix")
    private Boolean affix;

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
