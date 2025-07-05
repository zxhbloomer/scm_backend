package com.xinyirun.scm.bean.entity.busniess.settlement;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购结算表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_settlement")
public class BPoSettlementEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -503298411342484742L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 采购结算类型：0-采购结算
     */
    @TableField("type")
    private String type;

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    private String status;

    /**
     * 结算日期
     */
    @TableField("settlement_date")
    private java.time.LocalDate settlement_date;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    @TableField("settle_type")
    private String settle_type;

    /**
     * 结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    @TableField("bill_type")
    private String bill_type;

    /**
     * 付款方式：1-银行转账
     */
    @TableField("payment_type")
    private String payment_type;


    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    private String supplier_code;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplier_name;

    /**
     * 购买方ID
     */
    @TableField("purchaser_id")
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    @TableField("purchaser_code")
    private String purchaser_code;

    /**
     * 采购方名称
     */
    @TableField("purchaser_name")
    private String purchaser_name;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discount_amount;

    /**
     * 自动冲抵开关：true-预付款冲抵；false-不冲抵
     */
    @TableField("is_offset")
    private Boolean is_offset;

    /**
     * 其他金额
     */
    @TableField("other_amount")
    private BigDecimal other_amount;

    /**
     * 杂项金额
     */
    @TableField("misc_amount")
    private BigDecimal misc_amount;

    /**
     * 罚款金额
     */
    @TableField("penalty_amount")
    private BigDecimal penalty_amount;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    private Boolean is_del;

    /**
     * 流程状态
     */
    @TableField("next_approve_name")
    private String next_approve_name;

    /**
     * 流程实例ID
     */
    @TableField("bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    @TableField("bpm_instance_code")
    private String bpm_instance_code;

    /**
     * 审批流程名称：采购订单新增审批
     */
    @TableField("bpm_process_name")
    private String bpm_process_name;

    /**
     * 作废流程实例ID
     */
    @TableField("bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * 作废流程实例code
     */
    @TableField("bpm_cancel_instance_code")
    private String bpm_cancel_instance_code;

    /**
     * 作废审批流程名称：作废审批流程
     */
    @TableField("bpm_cancel_process_name")
    private String bpm_cancel_process_name;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;
} 