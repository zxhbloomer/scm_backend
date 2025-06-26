package com.xinyirun.scm.bean.entity.sys.pages;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 页面按钮表
 * </p>
 *
 * @author zxh
 * @since 2020-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_pages_function")
public class SPagesFunctionEntity implements Serializable {

    private static final long serialVersionUID = -7034286474283534783L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 页面id
     */
    @TableField("page_id")
    private Long page_id;

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
