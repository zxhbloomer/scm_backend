package com.xinyirun.scm.bean.entity.business.wms.out;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库单附件表
 * </p>
 *
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_out_attach")
public class BOutAttachEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8231630392927790150L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 出库单id
     */
    @TableField("out_id")
    private Integer out_id;

    /**
     * 磅单文件
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 出库明细附件
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 检验单
     */
    @TableField("three_file")
    private Integer three_file;

    /**
     * 货物照片
     */
    @TableField("four_file")
    private Integer four_file;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value = "dbversion")
    private Integer dbversion;

}