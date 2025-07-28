package com.xinyirun.scm.bean.entity.business.sync;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 同步状态
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_sync_status")
public class BSyncStatusEntity implements Serializable {

    private static final long serialVersionUID = -1679156055544019009L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 关联单号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 关联单号
     */
    @TableField("serial_code")
    private String serial_code;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 同步失败信息
     */
    @TableField("msg")
    private String msg;

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
    @TableField(value="dbversion")
    private Integer dbversion;

    @TableField("serial_detail_code")
    private String serial_detail_code;

    @TableField("serial_detail_id")
    private Integer serial_detail_id;
}
