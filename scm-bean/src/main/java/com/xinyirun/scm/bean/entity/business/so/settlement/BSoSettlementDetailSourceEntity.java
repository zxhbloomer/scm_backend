package com.xinyirun.scm.bean.entity.business.so.settlement;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 销售结算明细表-源单实体类
 * 
 * @author Claude Code
 * @since 2024-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_settlement_detail_source")
public class BSoSettlementDetailSourceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8032767730432234431L;
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
     * 销售结算主表code
     */
    @TableField("so_settlement_code")
    @DataChangeLabelAnnotation("销售结算编号")
    private String so_settlement_code;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款
     */
    @TableField("settle_type")
    @DataChangeLabelAnnotation("结算方式")
    private String settle_type;

    /**
     * 结算单据类型：1-实际发货结算；2-货转凭证结算
     */
    @TableField("bill_type")
    @DataChangeLabelAnnotation("结算单据类型")
    private String bill_type;

    /**
     * 项目编号
     */
    @TableField("project_code")
    @DataChangeLabelAnnotation("项目编号")
    private String project_code;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    @DataChangeLabelAnnotation("销售合同id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    @DataChangeLabelAnnotation("销售合同编号")
    private String so_contract_code;

    /**
     * 销售订单id
     */
    @TableField("so_order_id")
    @DataChangeLabelAnnotation("销售订单id")
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    @DataChangeLabelAnnotation("销售订单编号")
    private String so_order_code;

    /**
     * 销售订单明细id
     */
    @TableField("so_order_detail_id")
    @DataChangeLabelAnnotation("销售订单明细id")
    private Integer so_order_detail_id;
}