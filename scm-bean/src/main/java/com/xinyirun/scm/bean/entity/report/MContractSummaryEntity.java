package com.xinyirun.scm.bean.entity.report;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wenwl
 * @since 2021-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_contract_summary")
public class MContractSummaryEntity implements Serializable {

    private static final long serialVersionUID = 4776164989252010041L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物料分组
     */
    @TableField("material_group")
    private String material_group;

    @TableField("customer")
    private String customer;

    @TableField("bill_no")
    private String bill_no;

    @TableField("sal_dt")
    private LocalDateTime sal_dt;

    @TableField("specification")
    private String specification;

    @TableField("qty")
    private BigDecimal qty;

    @TableField("price")
    private BigDecimal price;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("out_qty")
    private BigDecimal out_qty;

    @TableField("rate")
    private BigDecimal rate;

    /**
     * 是否启用(1:true-已启用,0:false-已禁用)
     */
    @TableField("is_enable")
    private Boolean is_enable;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

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
