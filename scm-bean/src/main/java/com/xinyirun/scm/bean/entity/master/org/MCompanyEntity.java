package com.xinyirun.scm.bean.entity.master.org;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_company")
public class MCompanyEntity implements Serializable {

    private static final long serialVersionUID = -1373230930742993029L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 企业编号
     */
    @TableField("code")
    private String code;

    /**
     * 社会信用代码
     */
    @TableField("company_no")
    private String company_no;

    /**
     * 企业全称
     */
    @TableField("name")
    private String name;

    /**
     * 企业简称
     */
    @TableField("simple_name")
    private String simple_name;

    /**
     * 地址簿id
     */
    @TableField("address_id")
    private Long address_id;

    /**
     * 法定代表人
     */
    @TableField("juridical_name")
    private String juridical_name;

    /**
     * 注册资本
     */
    @TableField("register_capital")
    private BigDecimal register_capital;

    /**
     * =0,内资=1,国有全资=2,集体全资=3,股份合作=4,联营=5,国有联营=6,集体联营=7,国有与集体联营=8,其它联营=9,有限责任（公司）=10,国有独资（公司）=11,其它有限责任（公司）=12,股份有限（公司）=13,私有=14,私有独资=15,私有合伙=16,私营有限责任（公司）=17,个体经营=18,私营股份有限（公司）=19,其它私有=20,其它内资=21,内地与港、澳、台合作=22,内地与港、澳、台合资=23,港、澳、台投资=24,港、澳、台独资=25,港、澳、台投资股份有限（公司）=26,其他港、澳、台投资=27,外资=28,国外投资股份有限（公司）=29,其他国外投资=30,其他=31
     */
    @TableField("type")
    private String type;

    /**
     * 成立日期
     */
    @TableField("setup_date")
    private LocalDate setup_date;

    /**
     * 营业有效期
     */
    @TableField("end_date")
    private LocalDate end_date;

    /**
     * 营业执照 长期flg
     */
    @TableField("long_term")
    private Boolean long_term;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean is_del;

    /**
     * 租户id
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


}
