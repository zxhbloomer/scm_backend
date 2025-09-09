package com.xinyirun.scm.bean.entity.master.warehouse;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_location")
public class MLocationEntity implements Serializable {

    private static final long serialVersionUID = -4626625358142601038L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

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
     * 名称拼音首字母
     */
    @TableField("name_pinyin_initial")
    private String name_pinyin_initial;

    /**
     * 简称拼音首字母
     */
    @TableField("short_name_pinyin_initial")
    private String short_name_pinyin_initial;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 是否锁定盘点 :0否,1是
     */
    @TableField("inventory")
    private Boolean inventory;

    /**
     * 是否默认库位/库区:0否,1是
     */
    @TableField("is_default")
    private Boolean is_default;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态 0启用 1停用
     */
    @TableField("status")
    private Boolean status;

    /**
     * 状态 0启用 1停用
     */
    @TableField("enable")
    private Boolean enable;

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
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 是否删除：false-未删除，true-已删除
     */
    @TableField("is_del")
    private Boolean is_del;

}
