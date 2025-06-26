package com.xinyirun.scm.bean.system.vo.master.enterprise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MEnterpriseImportVo implements Serializable {

    private static final long serialVersionUID = 8179466261414373498L;


    /**
     * 企业信用代码
     */
    private String uscc;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 法人代表
     */
    private String legal_person;

    /**
     * 注册资金
     */
    private BigDecimal registration_capital;

    /**
     * 注册日期
     */
    private LocalDateTime registration_date;

    /**
     * 详细地址
     */
    private String address;


    /**
     * 联系电话
     */
    private String contact_number;

    /**
     * 联系人
     */
    private String contact_person;


    /**
     * 备注
     */
    private String remark;


    /**
     * 企业类型
     */
    private String type_ids_str;


    /**
     * 开票公司名称
     */
    private String bill_name;

    /**
     * 纳税人识别号
     */
    private String taxpayer_no;


    /**
     * 开户行
     */
    private String bank;

    /**
     * 银行账号
     */
    private String bank_account;

    /**
     * 开票地址
     */
    private String bill_address;

    /**
     * 开票电话
     */
    private String bill_phone;

    /**
     * 电票接受电话
     */
    private String bill_accept_phone;

    /**
     * 电票接受邮箱
     */
    private String bill_accept_mail;

    /**
     * 页面code
     */
    private String page_code;

     /**
     * url
     */
    private String url;

}
