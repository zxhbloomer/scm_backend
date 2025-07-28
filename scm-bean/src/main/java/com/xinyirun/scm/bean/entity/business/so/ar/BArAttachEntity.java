package com.xinyirun.scm.bean.entity.business.so.ar;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 应收账款附件表（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_attach")
public class BArAttachEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -5403616284249020171L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * b_ar id
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 附件文件
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建时间", extension = "getCTimeExtension")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;


}