package com.xinyirun.scm.bean.system.vo.master.customer;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
@NoArgsConstructor
// @ApiModel(value = "客户", description = "客户")
public class MCustomerVo implements Serializable {

    private static final long serialVersionUID = 8179466261414373498L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 客户编码
     */
    private String code;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户简称
     */
    private String short_name;

    /**
     * 客户名称拼音
     */
    private String name_pinyin;

    /**
     * 客户简称拼音
     */
    private String short_name_pinyin;

    /**
     * 企业性质（1民营，2国企，3合资，4外资）
     */
    private String scope;

    /**
     * 企业性质值
     */
    private String scope_name;

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
     * 客户来源值
     */
    private String source_name;

    /**
     * 客户类别：借款人，贷款人
     */
    private String type;

    /**
     * 客户类别值
     */
    private String type_name;

    /**
     * 法人代表
     */
    private String legal_person;

    /**
     * 工商注册号
     */
    private String registration_no;

    /**
     * 企业信用代码
     */
    private String credit_no;


    /**
     * 注册资金
     */
    private BigDecimal registration_capital;

    /**
     * 注册日期
     */
    private LocalDateTime registration_date;

    /**
     * 营业期限
     */
    private LocalDateTime end_date;

    /**
     * 是否长期
     */
    private Boolean is_long_period;

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
     * 是否集团货主
     */
    private Boolean is_org;

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
     * 是否启用（1启用0停用）
     */
    private Boolean enable;


    /**
     * 客户头像
     */
    private String picture;

    /**
     * 性别
     */
    private String gender;

    /**
     * 企业类型
     */
    private String mold;

    /**
     * 企业类型值
     */
    private String mold_name;


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
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

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

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    private List<Integer> ids;
}
