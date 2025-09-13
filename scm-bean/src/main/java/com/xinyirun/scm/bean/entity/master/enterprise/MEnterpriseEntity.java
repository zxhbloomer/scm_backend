package com.xinyirun.scm.bean.entity.master.enterprise;

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
 * 企业信息表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_enterprise")
@DataChangeEntityAnnotation(value="企业信息表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.master.enterprise.DataChangeStrategyMEnterpriseEntityServiceImpl")
public class MEnterpriseEntity implements Serializable {

    private static final long serialVersionUID = 577779939405016202L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 统一社会信用代码
     */
    @TableField("uscc")
    @DataChangeLabelAnnotation("统一社会信用代码")
    private String uscc;

    /**
     * 企业编码
     */
    @TableField("code")
    @DataChangeLabelAnnotation("企业编号")
    private String code;

    /**
     * 版本，0开始每次审批通过后累加1
     */
    @TableField("version")
    @DataChangeLabelAnnotation("版本")
    private Integer version;

    /**
     * 修改理由，在单据完成审批后，修改时需要记录修改理由
     */
    @TableField("modify_reason")
    @DataChangeLabelAnnotation("修改理由")
    private String modify_reason;

    /**
     * 企业名称
     */
    @TableField("name")
    @DataChangeLabelAnnotation("企业名称")
    private String name;

    /**
     * 企业名称全拼
     */
    @TableField("name_pinyin")
    private String name_pinyin;

    /**
     * 企业简称拼音
     */
    @TableField("name_short_pinyin")
    private String name_short_pinyin;

    /**
     * 注册资本
     */
    @TableField("registration_capital")
    @DataChangeLabelAnnotation("注册资本")
    private BigDecimal registration_capital;

    /**
     * 企业类型：1客户 2供应商 3仓储方 4承运商 5加工厂
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
    @TableField("legal_person_short_pinyin")
    private String legal_person_short_pinyin;

    /**
     * 成立时间
     */
    @TableField("est_date")
    @DataChangeLabelAnnotation("成立时间")
    private LocalDateTime est_date;

    /**
     * 详细地址
     */
    @TableField("address")
    @DataChangeLabelAnnotation("详细地址")
    private String address;

    /**
     * 联系人
     */
    @TableField("contact_person")
    @DataChangeLabelAnnotation("联系人")
    private String contact_person;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    @DataChangeLabelAnnotation("联系电话")
    private String contact_phone;

    /**
     * 备注
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("备注")
    private String remark;

    /**
     * 曾用名
     */
    @TableField("former_name")
    @DataChangeLabelAnnotation("曾用名")
    private String former_name;

    /**
     * 曾用名全拼
     */
    @TableField("former_name_pinyin")
    private String former_name_pinyin;

    /**
     * 曾用名简拼
     */
    @TableField("former_name_short_pinyin")
    private String former_name_short_pinyin;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    @DataChangeLabelAnnotation("删除0-未删除，1-已删除")
    private Boolean is_del;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
     */
    @TableField("status")
    @DataChangeLabelAnnotation(value="审核状态 0-待审核 1-审核中 2-审核通过 3-驳回", dictExtension = "getDictExtension", dictExtensionType = "com.xinyirun.scm.common.constant.DictConstant.DICT_M_ENTERPRISE_STATUS")
    private String status;

    /**
     * 置顶排序时间
     */
    @TableField(value="top_time")
    private LocalDateTime top_time;

    /**
     * 实例表ID
     */
    @TableField(value="bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    @TableField(value="bpm_instance_code")
    private String bpm_instance_code;

    /**
     * 作废流程实例ID
     */
    @TableField(value="bpm_cancel_instance_id")
    @DataChangeLabelAnnotation("作废流程实例ID")
    private Integer bpm_cancel_instance_id;

    /**
     * 作废流程实例code
     */
    @TableField(value="bpm_cancel_instance_code")
    @DataChangeLabelAnnotation("作废流程实例编码")
    private String bpm_cancel_instance_code;

    /**
     * 作废流程名称
     */
    @TableField(value="bpm_cancel_process_name")
    @DataChangeLabelAnnotation("作废流程名称")
    private String bpm_cancel_process_name;

    /**
     * 审批流程名称：企业新增审批
     */
    @TableField(value="bpm_process_name")
    private String bpm_process_name;

    /**
     * 流程状态
     */
    @TableField(value="next_approve_name")
    private String next_approve_name;

    /**
     * 作废理由
     */
    @TableField("cancel_reason")
    @DataChangeLabelAnnotation("作废理由")
    private String cancel_reason;

    /**
     * 主体企业：0-false（不是）、1-true（是）
     */
    @TableField(value="is_sys_company")
    private Boolean is_sys_company;

    /**
     * 主体企业编号
     */
    @TableField(value="sys_company_code")
    private Boolean sys_company_code;

    /**
     * 主体企业id
     */
    @TableField(value="sys_company_id")
    private Integer sys_company_id;

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

}
