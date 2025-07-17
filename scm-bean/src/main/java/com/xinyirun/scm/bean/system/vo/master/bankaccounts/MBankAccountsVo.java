package com.xinyirun.scm.bean.system.vo.master.bankaccounts;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
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
public class MBankAccountsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5840747088254885380L;

    private Integer id;

    /**
     * 主体企业id
     */
    private Integer enterprise_id;

    /**
     * 主体企业code
     */
    private String enterprise_code;

    /**
     * 主体企业名称
     */
    private String enterprise_name;

    /**
     * 编号
     */
    private String code;

    /**
     * 账户名称
     */
    private String name;

    /**
     * 币别：RMB
     */
    private String currency;

    /**
     * 开户名
     */
    private String holder_name;

    /**
     * 开户行
     */
    private String bank_name;

    /**
     * 银行账号
     */
    private String account_number;

    /**
     * 是否默认(0-否 1-是)
     */
    private Integer is_default;
    private String is_default_name;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态：0-禁用、1-可用、-1-删除
     */
    private String status;
    private String status_name;

    /**
     * 银企互联状态：0-未启用
     */
    private Integer link_status;
    private String link_status_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;
    private String c_name;

    /**
     * 修改人id
     */
    private Long u_id;
    private String u_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 校验类型
     */
    private String check_type;

    /**
     * 银行账户分类
     */
    private String accounts_purpose_type;

    /**
     * 银行账户分类code
     */
    private String bank_accounts_purpose_code;

    /**
     * 银行账户分类id
     */
    private Integer bank_accounts_purpose_id;

    /**
     * 银行账户分类名称
     */
    private String accounts_purpose_type_name;

    /**
     * 银行账户类型名称（关联m_bank_accounts_type表）
     */
    private String bank_type_name;

    /**
     * 导出 id
     */
    private Integer[] ids;

    /**
     * 银行账户id
     */
    private String bank_id;

    /**
     * 银行账户
     */
    private String bank_value;

    /**
     * 账户类型
     */
    private String [] bank_type;

}
