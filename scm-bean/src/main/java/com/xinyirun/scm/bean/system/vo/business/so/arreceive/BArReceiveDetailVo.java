package com.xinyirun.scm.bean.system.vo.business.so.arreceive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收单明细表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReceiveDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6147293856738291405L;

    private Integer id;

    /**
     * 应收单明细编号
     */
    private String code;

    /**
     * 应收单id
     */
    private Integer ar_receive_id;

    /**
     * 应收单code
     */
    private String ar_receive_code;

    /**
     * 应收账款表.id
     */
    private Integer ar_id;

    /**
     * 应收账款表.code
     */
    private String ar_code;

    /**
     * 应收账款明细表.id
     */
    private Integer ar_detail_id;

    /**
     * 应收账款明细表.code
     */
    private String ar_detail_code;

    /**
     * 企业银行账户表id
     */
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表编号
     */
    private String bank_accounts_code;

    /**
     * 应收金额
     */
    private BigDecimal receivable_amount;

    /**
     * 已收金额
     */
    private BigDecimal received_amount;

    /**
     * 本次收款金额
     */
    private BigDecimal receive_amount;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 收款账户名
     */
    private String name;

    /**
     * 开户行
     */
    private String bank_name;

    /**
     * 银行账号
     */
    private String account_number;

    /**
     * 收款中金额
     */
    private BigDecimal receiving_amount;

    /**
     * 未收款金额
     */
    private BigDecimal unreceive_amount;

    /**
     * 作废金额
     */
    private BigDecimal cancel_amount;

    /**
     * 备注
     */
    private String remark;
}