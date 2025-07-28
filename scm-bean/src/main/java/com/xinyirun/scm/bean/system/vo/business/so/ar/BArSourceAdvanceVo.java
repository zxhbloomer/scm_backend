package com.xinyirun.scm.bean.system.vo.business.so.ar;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收账款关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArSourceAdvanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5759429204273384832L;

    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 应收账款主表id
     */
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    private String ar_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    private String type;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String so_goods;

    /**
     * 总数量
     */
    private BigDecimal so_qty;

    /**
     * 总金额
     */
    private BigDecimal so_amount;

    /**
     * 累计预收款金额
     */
    private BigDecimal so_advance_payment_amount;

    /**
     * 可申请预收款金额
     */
    private BigDecimal so_can_advance_payment_amount;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 应收金额总计
     */
    private BigDecimal receivable_amount_total;

    /**
     * 已收款总金额
     */
    private BigDecimal received_amount_total;

    /**
     * 收款中总金额
     */
    private BigDecimal receiving_amount_total;

    /**
     * 未收款总金额
     */
    private BigDecimal unreceive_amount_total;

    /**
     * 中止收款总金额
     */
    private BigDecimal stopReceive_amount_total;

    /**
     * 取消收款总金额
     */
    private BigDecimal cancelReceive_amount_total;

    /**
     * 收款单主表id
     */
    private Integer ar_pay_id;

    /**
     * 收款主表code
     */
    private String ar_pay_code;

    /**
     * 累计预收款金额
     */
    private BigDecimal so_advance_receive_amount;

}