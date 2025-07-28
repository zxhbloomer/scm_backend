package com.xinyirun.scm.bean.entity.business.so.settlement;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售结算主表实体类
 * 
 * @author Claude Code
 * @since 2024-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_settlement")
public class BSoSettlementEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8497482356013602953L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    @DataChangeLabelAnnotation("编号")
    private String code;

    /**
     * 销售结算类型：0-销售结算
     */
    @TableField("type")
    @DataChangeLabelAnnotation("销售结算类型")
    private String type;

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    @DataChangeLabelAnnotation("状态")
    private String status;

    /**
     * 结算日期
     */
    @TableField("settlement_date")
    @DataChangeLabelAnnotation("结算日期")
    private LocalDate settlement_date;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款
     */
    @TableField("settle_type")
    @DataChangeLabelAnnotation("结算方式")
    private String settle_type;

    /**
     * 结算单据类型：1-实际发货结算；2-货转凭证结算
     */
    @TableField("bill_type")
    @DataChangeLabelAnnotation("结算单据类型")
    private String bill_type;

    /**
     * 收款方式：1-银行转账
     */
    @TableField("payment_type")
    @DataChangeLabelAnnotation("收款方式")
    private String payment_type;

    /**
     * 客户id
     */
    @TableField("customer_id")
    @DataChangeLabelAnnotation("客户")
    private Integer customer_id;

    /**
     * 客户编号
     */
    @TableField("customer_code")
    @DataChangeLabelAnnotation("客户编号")
    private String customer_code;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    @DataChangeLabelAnnotation("客户名称")
    private String customer_name;

    /**
     * 销售方id
     */
    @TableField("seller_id")
    @DataChangeLabelAnnotation("销售方")
    private Integer seller_id;

    /**
     * 销售方编号
     */
    @TableField("seller_code")
    @DataChangeLabelAnnotation("销售方编号")
    private String seller_code;

    /**
     * 销售方名称
     */
    @TableField("seller_name")
    @DataChangeLabelAnnotation("销售方名称")
    private String seller_name;

    /**
     * 备注
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("备注")
    private String remark;

    /**
     * 折扣金额
     */
    @TableField("discount_amount")
    @DataChangeLabelAnnotation("折扣金额")
    private BigDecimal discount_amount;

    /**
     * 其他金额
     */
    @TableField("other_amount")
    @DataChangeLabelAnnotation("其他金额")
    private BigDecimal other_amount;

    /**
     * 杂费金额
     */
    @TableField("misc_amount")
    @DataChangeLabelAnnotation("杂费金额")
    private BigDecimal misc_amount;

    /**
     * 违约金金额
     */
    @TableField("penalty_amount")
    @DataChangeLabelAnnotation("违约金金额")
    private BigDecimal penalty_amount;

    /**
     * 自动冲抵开关：1-预收款冲抵；0-不冲抵
     */
    @TableField("is_offset")
    @DataChangeLabelAnnotation("自动冲抵开关")
    private Boolean is_offset;

    /**
     * 删除标识：0-正常 1-删除
     */
    @TableField("is_del")
    @DataChangeLabelAnnotation("删除标识")
    private Boolean is_del;

    /**
     * 下一审批人姓名
     */
    @TableField("next_approve_name")
    @DataChangeLabelAnnotation("下一审批人姓名")
    private String next_approve_name;

    /**
     * BPM实例id
     */
    @TableField("bpm_instance_id")
    @DataChangeLabelAnnotation("BPM实例id")
    private Long bpm_instance_id;

    /**
     * BPM实例编号
     */
    @TableField("bpm_instance_code")
    @DataChangeLabelAnnotation("BPM实例编号")
    private String bpm_instance_code;

    /**
     * BPM流程名称
     */
    @TableField("bpm_process_name")
    @DataChangeLabelAnnotation("BPM流程名称")
    private String bpm_process_name;

    /**
     * BPM作废审批实例id
     */
    @TableField("bpm_cancel_instance_id")
    @DataChangeLabelAnnotation("BPM作废审批实例id")
    private Long bpm_cancel_instance_id;

    /**
     * BPM作废审批实例编号
     */
    @TableField("bpm_cancel_instance_code")
    @DataChangeLabelAnnotation("BPM作废审批实例编号")
    private String bpm_cancel_instance_code;

    /**
     * BPM作废审批流程名称
     */
    @TableField("bpm_cancel_process_name")
    @DataChangeLabelAnnotation("BPM作废审批流程名称")
    private String bpm_cancel_process_name;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建时间", extension = "getCTimeExtension")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
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
    @TableField("dbversion")
    @Version
    private Integer dbversion;
}