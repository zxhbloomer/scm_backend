package com.xinyirun.scm.bean.entity.master.bank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 企业银行账户-分类表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_bank_accounts_purpose")
public class MBankAccountsPurposeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 企业银行账户表id
     */
    @TableField("bank_accounts_id")
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表code
     */
    @TableField("bank_accounts_code")
    private String bank_accounts_code;

    /**
     * 类型（1-预付款、2-预收款、3-应付款、4-应收款、5-保证金、6-其他付款、7-其他收款）
     */
    @TableField("type")
    private String type;

    /**
     * 名称（如：资金池、预付款、预收款、应付款、应收款、其他付款、其他收款）
     */
    @TableField("name")
    private String name;


}
