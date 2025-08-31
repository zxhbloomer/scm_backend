package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 公司主表
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "公司主表", description = "公司主表")
@EqualsAndHashCode(callSuper=false)
public class MCompanyVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 2316479657097782895L;

    private Long id;

    /**
     * 企业编号
     */
    private String code;

    /**
     * 社会信用代码
     */
    private String company_no;
    /**
     * 企业全称
     */
    private String name;

    /**
     * 企业简称
     */
    private String simple_name;


    /**
     * 法定代表人
     */
    private String juridical_name;

    /**
     * 注册资本
     */
    private BigDecimal register_capital;

    /**
     * =0,内资=1,国有全资=2,集体全资=3,股份合作=4,联营=5,国有联营=6,集体联营=7,国有与集体联营=8,其它联营=9,有限责任（公司）=10,国有独资（公司）=11,其它有限责任（公司）=12,股份有限（公司）=13,私有=14,私有独资=15,私有合伙=16,私营有限责任（公司）=17,个体经营=18,私营股份有限（公司）=19,其它私有=20,其它内资=21,内地与港、澳、台合作=22,内地与港、澳、台合资=23,港、澳、台投资=24,港、澳、台独资=25,港、澳、台投资股份有限（公司）=26,其他港、澳、台投资=27,外资=28,国外投资股份有限（公司）=29,其他国外投资=30,其他=31
     */
    private String type;
    private String type_name;

    /**
     * 成立日期
     */
    private LocalDate setup_date;

    /**
     * 营业有效期
     */
    private LocalDate end_date;

    /**
     * 营业执照 长期flg
     */
    private Boolean long_term;

    /**
     * 说明
     */
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;
    private String is_del_name;

    /**
     * 地址簿id
     */
    private Long address_id;

    /**
     * 邮编
     */
    private String postal_code;

    /**
     * 省
     */
    private Integer province_code;

    /**
     * 市
     */
    private Integer city_code;

    /**
     * 区
     */
    private Integer area_code;

    /**
     * 详细地址
     */
    private String detail_address;


    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 关联单号
     */
    private Long parent_serial_id;

    /**
     * 关联单号类型
     */
    private String parent_serial_type;
    /**
     * 集团名称（用于查询条件）
     */
    private String group_name;
    
    /**
     * 集团简称（用于页面显示）
     */
    private String group_simple_name;
    private String parent_type_text;

    /**
     * 弹出框模式：空：普通模式；10：组织使用，需要排除已经选择的数据；
     */
    private String dataModel;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 选中导出时的ID数组
     */
    private Long[] ids;
}
