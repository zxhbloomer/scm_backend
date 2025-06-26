package com.xinyirun.scm.bean.entity.master.bank;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业银行账户表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_bank_accounts")
public class MBankAccountsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -7027232354653530758L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 主体企业id
     */
    @TableField("enterprise_id")
    private Integer enterprise_id;

    /**
     * 主体企业code
     */
    @TableField("enterprise_code")
    private String enterprise_code;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 账户名称
     */
    @TableField("name")
    private String name;

    /**
     * 币别：RMB
     */
    @TableField("currency")
    private String currency;

    /**
     * 开户名
     */
    @TableField("holder_name")
    private String holder_name;

    /**
     * 开户行
     */
    @TableField("bank_name")
    private String bank_name;

    /**
     * 银行账号
     */
    @TableField("account_number")
    private String account_number;

    /**
     * 是否默认(0-否 1-是)
     */
    @TableField("is_default")
    private Integer is_default;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 状态：0-禁用、1-可用、-1-删除
     */
    @TableField("status")
    private String status;

    /**
     * 银企互联状态：0-未启用
     */
    @TableField("link_status")
    private Integer link_status;

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
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

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
    private Integer dbversion;


}
