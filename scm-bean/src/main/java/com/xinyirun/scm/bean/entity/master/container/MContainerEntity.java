package com.xinyirun.scm.bean.entity.master.container;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 集装箱管理
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_container")
public class MContainerEntity implements Serializable {

    private static final long serialVersionUID = 2254841438980538525L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * 集装箱号
     */
    @TableField("code")
    private String code;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Integer is_del;

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
    @TableField("dbversion")
    @Version
    private Integer dbversion;


}
