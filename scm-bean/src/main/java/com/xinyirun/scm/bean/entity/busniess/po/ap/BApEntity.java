package com.xinyirun.scm.bean.entity.busniess.po.ap;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付账款管理表（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap")
public class BApEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -790798399084984530L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 1-应付、2-预付、3-其他支出
     */
    @TableField("type")
    private String type;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    private String status;

    /**
     * 付款状态：0-未付款、1-部分付款、2-已付款、-1-中止付款
     */
    @TableField("pay_status")
    private String pay_status;

    /**
     * 关联项目编号
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 采购合同编号
     */
    @TableField("po_contract_code")
    private String po_contract_code;

    /**
     * 采购合同id
     */
    @TableField("po_contract_id")
    private Integer po_contract_id;

    /**
     * 采购订单id
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 采购订单编号
     */
    @TableField("po_order_code")
    private String po_order_code;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    @DataChangeLabelAnnotation("供应商ID")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    @DataChangeLabelAnnotation("供应商编码")
    private String supplier_code;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    @DataChangeLabelAnnotation("供应商名称")
    private String supplier_name;

    /**
     * 购买方ID
     */
    @TableField("purchaser_id")
    @DataChangeLabelAnnotation("购买方ID")
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    @TableField("purchaser_code")
    @DataChangeLabelAnnotation("采购方编码")
    private String purchaser_code;

    /**
     * 采购方名称
     */
    @TableField("purchaser_name")
    @DataChangeLabelAnnotation("采购方名称")
    private String purchaser_name;

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
     * 中止理由
     */
    @TableField("stoppay_reason")
    private String stoppay_reason;

    /**
     * 中止操作人ID
     */
    @TableField("stoppay_u_id")
    private Integer stoppay_u_id;

    /**
     * 中止操作时间
     */
    @TableField("stoppay_u_time")
    private LocalDateTime stoppay_u_time;

    /**
     * 中止附件 ID
     */
    @TableField("stoppay_file")
    private Integer stoppay_file;

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
    @TableField("dbversion")
    private Integer dbversion;


}
