package com.xinyirun.scm.bean.entity.business.so.arrefund;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收退款管理表（Accounts Receivable）
 * </p>
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_refund")
public class BArReFundEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -790798399084984531L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    @TableField("type")
    private String type;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    private String status;

    /**
     * 退款状态：0-未退款、1-部分退款、2-已退款、-1-中止退款
     */
    @TableField("refund_status")
    private String refund_status;

    /**
     * 关联项目编号
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 销售合同ID
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    private String so_contract_code;

    /**
     * 销售订单ID
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    private String so_order_code;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Integer customer_id;

    /**
     * 客户编码
     */
    @TableField("customer_code")
    private String customer_code;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customer_name;

    /**
     * 销售方ID
     */
    @TableField("seller_id")
    private Integer seller_id;

    /**
     * 销售方编码
     */
    @TableField("seller_code")
    private String seller_code;

    /**
     * 销售方名称
     */
    @TableField("seller_name")
    private String seller_name;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    @DataChangeLabelAnnotation("删除0-未删除，1-已删除")
    private Boolean is_del;

    /**
     * 流程状态
     */
    @TableField(value="next_approve_name")
    private String next_approve_name;

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
     * 审批流程名称
     */
    @TableField(value="bpm_process_name")
    private String bpm_process_name;

    /**
     * 作废 实例表ID
     */
    @TableField(value="bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * 作废 流程实例code
     */
    @TableField(value="bpm_cancel_instance_code")
    private String bpm_cancel_instance_code;

    /**
     * 作废 审批流程名称
     */
    @TableField(value="bpm_cancel_process_name")
    private String bpm_cancel_process_name;

    /**
     * 作废理由
     */
    @TableField("cancel_reason")
    private String cancel_reason;

    /**
     * 作废附件 ID
     */
    @TableField("cancel_file")
    private Integer cancel_file;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
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
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}