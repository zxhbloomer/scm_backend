package com.xinyirun.scm.bean.entity.business.so.arrefund;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 应收退款关联单据表-源单
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_refund_source")
public class BArReFundSourceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1325791178665355608L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收退款主表id
     */
    @TableField("ar_refund_id")
    private Integer ar_refund_id;

    /**
     * 应收退款主表code
     */
    @TableField("ar_refund_code")
    private String ar_refund_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    @TableField("type")
    private String type;

    /**
     * 项目编号
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 销售合同ID
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    private String so_contract_code;

    /**
     * 销售订单ID
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    private String so_order_code;

}