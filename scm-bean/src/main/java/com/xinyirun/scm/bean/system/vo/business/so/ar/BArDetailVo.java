package com.xinyirun.scm.bean.system.vo.business.so.ar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收账款管理表明细（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArDetailVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -4297890846625345576L;

    private Integer id;

    /**
     * 编号
     */
    private String code;


    /**
     * 主表id
     */
    private Integer ar_id;


    /**
     * 主表code
     */
    private String ar_code;

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
     * 实收金额
     */
    private BigDecimal received_amount;

    /**
     * 本次收款金额
     */
    private BigDecimal receive_amount;

    /**
     * 收款中金额
     */
    private BigDecimal receiving_amount;

    /**
     * 未收款金额
     */
    private BigDecimal unreceive_amount;

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
     * 企业银行账户表名称
     */
    private String name;

    /**
     * 开户行
     */
    private String bank_name;

    /**
     * 银行账户
     */
    private String account_number;

    /**
     * 账户类型
     */
    private String bank_type_name;

}