package com.xinyirun.scm.bean.entity.business.wms.warehouse;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 仓库组一级分类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_warehouse_group")
public class BWarehouseGroupEntity implements Serializable {

    private static final long serialVersionUID = 6539359998999505380L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 类型：1一级；2二级；3三级
     */
    @TableField("type")
    private String type;

    /**
     * 简称
     */
    @TableField("short_name")
    private String short_name;

    /**
     * 名称拼音
     */
    @TableField("name_pinyin")
    private String name_pinyin;

    /**
     * 简称拼音
     */
    @TableField("short_name_pinyin")
    private String short_name_pinyin;

    /**
     * 名称简拼
     */
    @TableField("name_pinyin_abbr")
    private String name_pinyin_abbr;

    /**
     * 简称简拼
     */
    @TableField("short_name_pinyin_abbr")
    private String short_name_pinyin_abbr;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}
