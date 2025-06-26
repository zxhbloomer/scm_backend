package com.xinyirun.scm.bean.entity.sys.syscode;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 编码控制
 * </p>
 *
 * @author zxh
 * @since 2019-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_code")
@DataChangeEntityAnnotation(value="编码控制表", type = "main")
public class SCodeEntity implements Serializable {

    private static final long serialVersionUID = -3616976089046858264L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编码类型
     */
    @TableField("type")
    private String type;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 编码规则
     */
    @TableField("rule")
    private String rule;

    /**
     * 当前编码
     */
    @TableField("code")
    private String code;

    /**
     * 代码增加序号
     */
    @TableField("auto_create")
    private Long auto_create;

    /**
     * 前缀
     */
    @TableField("prefex")
    private String prefex;


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
