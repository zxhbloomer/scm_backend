package com.xinyirun.scm.bean.entity.business.so.arreceive;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应收来源预收表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_receive_source_advance")
public class BArReceiveSourceAdvanceEntity implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1942835746820957163L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收单主表id
     */
    @TableField("ar_receive_id")
    private Integer ar_receive_id;

    /**
     * 应收主表code
     */
    @TableField("ar_receive_code")
    private String ar_receive_code;

    /**
     * 应收账款主表id
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    @TableField("ar_code")
    private String ar_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    @TableField("type")
    private String type;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    private String so_contract_code;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    private String so_order_code;

    /**
     * 销售订单id
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    @TableField("so_goods")
    private String so_goods;

    /**
     * 总数量
     */
    @TableField("so_qty")
    private BigDecimal so_qty;

    /**
     * 总金额
     */
    @TableField("so_amount")
    private BigDecimal so_amount;

    /**
     * 累计预收款金额
     */
    @TableField("so_advance_receive_amount")
    private BigDecimal so_advance_receive_amount;

    /**
     * 本次申请金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

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
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}