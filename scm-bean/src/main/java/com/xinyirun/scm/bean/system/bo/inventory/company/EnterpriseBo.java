package com.xinyirun.scm.bean.system.bo.inventory.company;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;

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
public class EnterpriseBo implements Serializable {

    private static final long serialVersionUID = 577779939405016202L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 统一社会信用代码
     */
    private String uscc;

    /**
     * 企业编码
     */
    private String code;

    /**
     * 版本，0开始每次审批通过后累加1
     */
    private Integer version;

    /**
     * 修改理由，在单据完成审批后，修改时需要记录修改理由
     */
    private String modify_reason;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 企业名称全拼
     */
    private String name_pinyin;

    /**
     * 企业简称拼音
     */
    private String name_short_pinyin;

    /**
     * 注册资本
     */
    private BigDecimal registration_capital;

    /**
     * 企业类型：1客户 2供应商 3仓储方 4承运商 5加工厂
     */
    private String type;

    /**
     * 法人代表
     */
    private String legal_person;

    /**
     * 法人代表 全拼
     */
    private String legal_person_pinyin;

    /**
     * 法人代表 简拼
     */
    private String legal_person_short_pinyin;

    /**
     * 成立时间
     */
    private LocalDateTime est_date;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contact_person;

    /**
     * 联系电话
     */
    private String contact_phone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 曾用名
     */
    private String former_name;

    /**
     * 曾用名全拼
     */
    private String former_name_pinyin;

    /**
     * 曾用名简拼
     */
    private String former_name_short_pinyin;

    /**
     * 删除0-未删除，1-已删除
     */
    private Boolean is_del;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
     */
    private String status;

    /**
     * 置顶排序时间
     */
    private LocalDateTime top_time;

    /**
     * 实例表ID
     */
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 审批流程名称：企业新增审批
     */
    private String bpm_process_name;

    /**
     * 流程状态
     */
    private String next_approve_name;

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
    @TableField(value="dbversion", fill = FieldFill.INSERT_UPDATE)
    private Integer dbversion;

}
