package com.xinyirun.scm.bean.entity.sys.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_table_column_config")
public class STableColumnConfigEntity implements Serializable {

    private static final long serialVersionUID = -6934386302210997740L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 表格code
     */
    @TableField("table_code")
    private String table_code;

    /**
     * 表格id
     */
    @TableField("table_id")
    private Integer table_id;

    /**
     * 字段名
     */
    @TableField("name")
    private String name;

    /**
     * 表头名
     */
    @TableField("label")
    private String label;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 不可排序
     */
    @TableField("fix")
    private Boolean fix;

    /**
     * 是否显示
     */
    @TableField("is_enable")
    private Boolean is_enable;

    /**
     * 是否删除
     */
    @TableField("is_delete")
    private Boolean is_delete;

    /**
     * 分组名称
     */
    @TableField("group_name")
    private String group_name;

    /**
     * 分组内排序
     */
    @TableField("group_sort")
    private Integer group_sort;

    /**
     * 是否为组头
     */
    @TableField("is_group_header")
    private Integer is_group_header;
}
