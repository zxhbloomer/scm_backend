package com.xinyirun.scm.bean.entity.sys.config.dict;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典数据表
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_dict_data")
public class SDictDataEntity extends BaseEntity<SDictDataEntity> implements Serializable {

    private static final long serialVersionUID = -7176009605501263947L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型表code
     */
    @TableField("code")
    private String code;

    /**
     * 字典类型表id主键
     */
    @TableField("dict_type_id")
    private Long dict_type_id;

    /**
     * 字典排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 字典标签
     */
    @TableField("label")
    private String label;

    /**
     * 字典键值
     */
    @TableField("dict_value")
    private String dict_value;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 额外配置1～4
     */
    @TableField("extra1")
    private String extra1;
    @TableField("extra2")
    private String extra2;
    @TableField("extra3")
    private String extra3;
    @TableField("extra4")
    private String extra4;

    /**
     * 是否删除
     */
    @TableField(value = "is_del", fill = FieldFill.INSERT)
    private Boolean is_del;

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
