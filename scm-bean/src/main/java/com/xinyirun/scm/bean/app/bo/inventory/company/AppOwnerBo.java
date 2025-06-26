package com.xinyirun.scm.bean.app.bo.inventory.company;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppOwnerBo implements Serializable {


    private static final long serialVersionUID = -7912348917992002167L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 客户编码
     */
    private String code;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户简称
     */
    private String short_name;

    /**
     * 企业性质（1民营，2国企，3合资，4外资）
     */
    private String scope;

    /**
     * 营业期限
     */
    private LocalDateTime end_date;

    /**
     * 是否长期
     */
    private Boolean is_long_period;

    /**
     * 是否集团货主
     */
    private Boolean is_org;

    /**
     * 企业类型：1有限责任公司（自然人投资或控股），2股份有限公司分公司（上市、国有股份）
     */
    private String mold;

    /**
     * 企业规模
     */
    private String scale;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 客户来源（1独立开发）
     */
    private String source;

    /**
     * 客户类别：1借款方，2贷款方，3担保方，4管理企业，5其他
     */
    private String type;

    /**
     * 法人代表
     */
    private String legal_person;

    /**
     * 工商注册号
     */
    private String registration_no;

    /**
     * 营业执照号
     */
    private String business_license_no;

    /**
     * 税务登记号
     */
    private String tax_registration_no;

    /**
     * 注册资金
     */
    private BigDecimal registration_capital;

    /**
     * 注册日期
     */
    private LocalDateTime registration_date;

    /**
     * 经营范围
     */
    private String operation_scope;

    /**
     * 注册地址
     */
    private String registered_ddress;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邮编
     */
    private String postal_code;

    /**
     * 联系电话
     */
    private String contact_number;

    /**
     * 网址
     */
    private String website;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 是否删除（1删除0未删除）
     */
    private Boolean deleteflag;

    /**
     * 客户头像
     */
    private String picture;

    /**
     * 性别
     */
    private String gender;

    /**
     * 所属集团货主id
     */
    private Integer superior_id;

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

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
