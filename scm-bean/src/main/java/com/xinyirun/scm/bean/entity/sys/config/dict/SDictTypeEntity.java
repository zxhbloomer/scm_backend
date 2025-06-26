package com.xinyirun.scm.bean.entity.sys.config.dict;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典类型表、字典主表
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = false)
@TableName("s_dict_type")
public class SDictTypeEntity extends BaseEntity<SDictTypeEntity> implements Serializable {

    private static final long serialVersionUID = 2711906378885817609L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典名称
     */
    @TableField("name")
    private String name;

    /**
     * 字典类型：唯一
     */
    @TableField("code")
    private String code;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

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
