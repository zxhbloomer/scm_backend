package com.xinyirun.scm.bean.entity.busniess.aprefundpay;

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
 * 应付退款单明细表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_pay_detail")
public class BApReFundPayDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 967265783738673997L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退款单id
     */
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 退款单code
     */
    @TableField("ap_refund_code")
    private String ap_refund_code;

    /**
     * 采购合同编号
     */
    @TableField("po_contract_code")
    private String po_contract_code;

    /**
     * 采购订单编号
     */
    @TableField("po_code")
    private String po_code;

    /**
     * 应付账款单id
     */
    @TableField("ap_id")
    private Integer ap_id;

    /**
     * 应付账款单code
     */
    @TableField("ap_code")
    private String ap_code;


}
