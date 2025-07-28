package com.xinyirun.scm.bean.entity.business.so.settlement;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 销售结算附件表实体类
 * 
 * @author Claude Code
 * @since 2024-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_settlement_attach")
public class BSoSettlementAttachEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -959539802591956518L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售结算主表id
     */
    @TableField("so_settlement_id")
    @DataChangeLabelAnnotation("销售结算主表id")
    private Integer so_settlement_id;

    /**
     * 销售结算附件
     */
    @TableField("one_file")
    @DataChangeLabelAnnotation("销售结算附件")
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
    @TableField("dbversion")
    @Version
    private Integer dbversion;
}