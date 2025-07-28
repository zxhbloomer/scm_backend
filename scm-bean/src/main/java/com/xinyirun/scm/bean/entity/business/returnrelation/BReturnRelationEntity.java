package com.xinyirun.scm.bean.entity.business.returnrelation;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 退货表
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_return_relation")
public class BReturnRelationEntity implements Serializable {

    private static final long serialVersionUID = -6103172857465122980L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * code
     */
    @TableField("code")
    private String code;

    /**
     * 1-审核通过 2=作废
     */
    @TableField("status")
    private String status;

    /**
     * 关联单号ID
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 关联表名
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 关联单号CODE
     */
    @TableField("serial_code")
    private String serial_code;

    /**
     * 单据类型
     */
    @TableField("serial_type_name")
    private String serial_type_name;

    /**
     * 监管退货入库计划id
     */
    @TableField("in_plan_id")
    private Integer in_plan_id;

    /**
     * 监管退货入库计划code
     */
    @TableField("in_plan_code")
    private String in_plan_code;

    /**
     * 监管退货 退货单id
     */
    @TableField("in_id")
    private Integer in_id;

    /**
     * 监管退货 退货单code
     */
    @TableField("in_code")
    private String in_code;

    /**
     * 退货数量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 退货理由
     */
    @TableField("quantity_reason")
    private String quantity_reason;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 附件ID
     */
    @TableField("files_id")
    private Integer files_id;

    /**
     * 创建时间 
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

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
    private String dbversion;


}
