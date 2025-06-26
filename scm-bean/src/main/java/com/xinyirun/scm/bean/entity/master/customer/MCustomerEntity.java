package com.xinyirun.scm.bean.entity.master.customer;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_customer")
@DataChangeEntityAnnotation(value="企业信息表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.master.DataChangeStrategyMCustomerEntityServiceImpl")
public class MCustomerEntity implements Serializable {

    private static final long serialVersionUID = 577779939405016202L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @DataChangeLabelAnnotation("表id")
    private Integer id;

    /**
     * 客户编码
     */
    @TableField("code")
    @DataChangeLabelAnnotation("表code")
    private String code;

    /**
     * 统一社会信用代码
     */
    @TableField("credit_no")
    @DataChangeLabelAnnotation("统一社会信用代码")
    private String credit_no;

    /**
     * 货主编码
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 客户名称
     */
    @TableField("name")
    @DataChangeLabelAnnotation("客户名称")
    private String name;

    /**
     * 曾用名
     */
    @TableField("former_name")
    @DataChangeLabelAnnotation("曾用名")
    private String former_name;

    /**
     * 客户名称拼音
     */
    @TableField("name_pinyin")
    private String name_pinyin;

    /**
     * 客户名称拼音-首字母
     */
    @TableField("name_pinyin_first_letter")
    private String name_pinyin_first_letter;

    /**
     * 客户简称拼音
     */
    @TableField("short_name_pinyin")
    private String short_name_pinyin;


    /**
     * 客户简称拼音-首字母
     */
    @TableField("short_name_pinyin_first_letter")
    private String short_name_pinyin_first_letter;

    /**
     * 曾用名拼音
     */
    @TableField("former_name_pinyin")
    private String former_name_pinyin;

    /**
     * 曾用名拼音-首字母
     */
    @TableField("former_name_first_letter")
    private String former_name_pinyin_first_letter;


    /**
     * 审核状态 0-待审核 1-审核中 2-审核通过 3-驳回
     */
    @TableField("status")
    @DataChangeLabelAnnotation("审核状态 0-待审核 1-审核中 2-审核通过 3-驳回")
    private String status;

    /**
     * 审核意见
     */
    @TableField("audit_opinion")
    @DataChangeLabelAnnotation("审核意见")
    private String audit_opinion;

    /**
     * 企业性质（1民营，2国企，3合资，4外资）
     */
    @TableField("scope")
    private String scope;

    /**
     * 营业期限
     */
    @TableField("end_date")
    private LocalDateTime end_date;

    /**
     * 是否长期
     */
    @TableField("is_long_period")
    private Boolean is_long_period;

    /**
     * 是否集团货主
     */
    @TableField("is_org")
    private Boolean is_org;

    /**
     * 企业类型：1有限责任公司（自然人投资或控股），2股份有限公司分公司（上市、国有股份）
     */
    @TableField("mold")
    private String mold;

    /**
     * 企业规模
     */
    @TableField("scale")
    private String scale;

    /**
     * 所属行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 客户来源（1独立开发）
     */
    @TableField("source")
    private String source;

    /**
     * 客户类别：1借款方，2贷款方，3担保方，4管理企业，5其他
     */
    @TableField("type")
    private String type;

    /**
     * 法人代表
     */
    @TableField("legal_person")
    @DataChangeLabelAnnotation("法人代表")
    private String legal_person;

    /**
     * 法人代表 全拼
     */
    @TableField("legal_person_pinyin")
    private String legal_person_pinyin;

    /**
     * 法人代表 简拼
     */
    @TableField("short_legal_person_pinyin")
    private String short_legal_person_pinyin;

    /**
     * 社会信用代码
     */
    @TableField("registration_no")
    private String registration_no;


    /**
     * 注册资金
     */
    @TableField("registration_capital")
    @DataChangeLabelAnnotation("注册资金")
    private BigDecimal registration_capital;

    /**
     * 注册日期
     */
    @TableField("registration_date")
    @DataChangeLabelAnnotation("注册日期")
    private LocalDateTime registration_date;

    /**
     * 经营范围
     */
    @TableField("operation_scope")
    private String operation_scope;

    /**
     * 注册地址
     */
    @TableField("registered_ddress")
    @DataChangeLabelAnnotation("注册地址")
    private String registered_ddress;

    /**
     * 省
     */
    @TableField("province")
    private String province;

    /**
     * 市
     */
    @TableField("city")
    private String city;

    /**
     * 区
     */
    @TableField("district")
    private String district;

    /**
     * 详细地址
     */
    @TableField("address")
    @DataChangeLabelAnnotation("详细地址")
    private String address;

    /**
     * 邮编
     */
    @TableField("postal_code")
    private String postal_code;

    /**
     * 联系人
     */
    @TableField("contact_person")
    @DataChangeLabelAnnotation("联系人")
    private String contact_person;


    /**
     * 联系人 全拼
     */
    @TableField("contact_person_pinyin")
    private String contact_person_pinyin;

    /**
     * 联系人 简拼
     */
    @TableField("short_contact_person_pinyin")
    private String short_contact_person_pinyin;

    /**
     * 联系电话
     */
    @TableField("contact_number")
    @DataChangeLabelAnnotation("联系电话")
    private String contact_number;

    /**
     * 网址
     */
    @TableField("website")
    private String website;

    /**
     * 备注
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("备注")
    private String remark;

    /**
     * 是否启用
     */
    @TableField(value="enable", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(" 是否启用")
    private Boolean enable;

    /**
     * 客户头像
     */
    @TableField("picture")
    private String picture;

    /**
     * 性别
     */
    @TableField("gender")
    private String gender;

    /**
     * 所属集团货主id
     */
    @TableField("superior_id")
    private Integer superior_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
//    @DataChangeLabelAnnotation("创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
//    @DataChangeLabelAnnotation("修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 置顶排序时间
     */
    @TableField(value="top_time")
    private LocalDateTime top_time;

    /**
     * 纳税人识别号
     */
    @TableField(value="taxpayer_no")
    @DataChangeLabelAnnotation("纳税人识别号")
    private String taxpayer_no;

    /**
     * 开户行
     */
    @TableField(value="bank")
    @DataChangeLabelAnnotation("开户行")
    private String bank;

    /**
     * 银行账号
     */
    @TableField(value="bank_account")
    @DataChangeLabelAnnotation("银行账号")
    private String bank_account;

    /**
     * 开票公司名称
     */
    @TableField(value="bill_name")
    @DataChangeLabelAnnotation("开票公司名称")
    private String bill_name;

    /**
     * 开票地址
     */
    @TableField(value="bill_address")
    @DataChangeLabelAnnotation("开票地址")
    private String bill_address;

    /**
     * 开票电话
     */
    @TableField(value="bill_phone")
    @DataChangeLabelAnnotation("开票电话")
    private String bill_phone;

    /**
     * 电票接受电话
     */
    @TableField(value="bill_accept_phone")
    @DataChangeLabelAnnotation("电票接受电话")
    private String bill_accept_phone;

    /**
     * 电票接受邮箱
     */
    @TableField(value="bill_accept_mail")
    @DataChangeLabelAnnotation("电票接受邮箱")
    private String bill_accept_mail;

    /**
     * 是否黑名单 0-否 1-是
     */
    @TableField(value="blacklist")
    @DataChangeLabelAnnotation("是否黑名单")
    private Boolean blacklist;

    /**
     * 实例表ID
     */
    @TableField(value="bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * 流程状态
     */
    @TableField(value="next_approve_name")
    private String next_approve_name;
}
