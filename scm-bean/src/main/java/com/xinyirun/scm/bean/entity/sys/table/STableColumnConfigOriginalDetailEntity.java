package com.xinyirun.scm.bean.entity.sys.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 表格列配置原始详情表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_table_column_config_original_detail")
public class STableColumnConfigOriginalDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -2847352951048376921L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联original表ID
     */
    @TableField("original_id")
    private Integer original_id;

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
     * 是否显示
     */
    @TableField("is_enable")
    private Boolean is_enable;

    /**
     * 是否删除
     */
    @TableField("is_delete")
    private Boolean is_delete;
}