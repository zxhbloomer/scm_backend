package com.xinyirun.scm.bean.entity.master.customer;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *  货主
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_owner")
public class MOwnerEntity implements Serializable {

    private static final long serialVersionUID = 479589930405016201L;


    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 货主编码
     */
    @TableField("code")
    private String code;

    /**
     * 信用代码证
     */
    @TableField("credit_no")
    private String credit_no;

    /**
     * 货主名称
     */
    @TableField("name")
    private String name;

    /**
     * 货主简称
     */
    @TableField("short_name")
    private String short_name;

    /**
     * 货主名称拼音
     */
    @TableField("name_pinyin")
    private String name_pinyin;

    /**
     * 货主简称拼音
     */
    @TableField("short_name_pinyin")
    private String short_name_pinyin;

    /**
     * 板块
     */
    @TableField("business_type")
    private String business_type;

    /**
     * 是否启用
     */
    @TableField(value="enable", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
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
}
