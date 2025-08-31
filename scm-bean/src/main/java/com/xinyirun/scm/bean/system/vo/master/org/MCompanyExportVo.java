package com.xinyirun.scm.bean.system.vo.master.org;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
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
// @ApiModel(value = "公司主表导出Bean", description = "公司主表导出Bean")
@EqualsAndHashCode(callSuper=false)
public class MCompanyExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -70599579335943322L;
    
    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    /**
     * 集团简称（用于页面显示）
     */
    @ExcelProperty(value = "集团信息", index = 1)
    private String group_simple_name;

    /**
     * 企业编号
     */
    @ExcelProperty(value = "企业编号", index = 2)
    private String code;

    /**
     * 社会信用代码
     */
    @ExcelProperty(value = "社会信用代码", index = 3)
    private String company_no;

    /**
     * 企业名称
     */
    @ExcelProperty(value = "企业名称", index = 4)
    private String name;

    /**
     * 企业简称
     */
    @ExcelProperty(value = "企业简称", index = 5)
    private String simple_name;

    /**
     * 法定代表人
     */
    @ExcelProperty(value = "法定代表人", index = 6)
    private String juridical_name;

    /**
     * 注册资本
     */
    @ExcelProperty(value = "注册资本（万）", index = 7)
    private BigDecimal register_capital;

    /**
     * =0,内资=1,国有全资=2,集体全资=3,股份合作=4,联营=5,国有联营=6,集体联营=7,国有与集体联营=8,其它联营=9,有限责任（公司）=10,国有独资（公司）=11,其它有限责任（公司）=12,股份有限（公司）=13,私有=14,私有独资=15,私有合伙=16,私营有限责任（公司）=17,个体经营=18,私营股份有限（公司）=19,其它私有=20,其它内资=21,内地与港、澳、台合作=22,内地与港、澳、台合资=23,港、澳、台投资=24,港、澳、台独资=25,港、澳、台投资股份有限（公司）=26,其他港、澳、台投资=27,外资=28,国外投资股份有限（公司）=29,其他国外投资=30,其他=31
     */
    @ExcelIgnore
    private String type;
    @ExcelProperty(value = "企业类型", index = 8)
    private String type_name;

    /**
     * 成立日期
     */
    @ExcelProperty(value = "成立日期", index = 9)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDate setup_date;

    /**
     * 营业有效期（不在前端列表显示，不导出）
     */
    @ExcelIgnore
    private LocalDate end_date;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 10)
    private String descr;

    /**
     * 是否删除（不在前端列表显示，不导出）
     */
    @ExcelIgnore
    private Boolean is_del;
    
    @ExcelIgnore
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    @ExcelIgnore
    private Long c_id;

    /**
     * 新增人（不在前端列表显示，不导出）
     */
    @ExcelIgnore
    private String c_name;
    
    /**
     * 新增时间（不在前端列表显示，不导出）
     */
    @ExcelIgnore
    private LocalDateTime c_time;

    @ExcelIgnore
    private Long u_id;

    @ExcelProperty(value = "更新人", index = 11)
    private String u_name;
    @ExcelProperty(value = "更新时间", index = 12)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @ExcelIgnore
    private Integer dbversion;


}
