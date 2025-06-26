package com.xinyirun.scm.bean.entity.busniess.poorder;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 采购订单表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_order")
public class BPoOrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号，自动生成
     */
    @TableField("code")
    @DataChangeLabelAnnotation("订单编号")
    private String code;

    /**
     * 合同编号
     */
    @TableField("po_contract_code")
    @DataChangeLabelAnnotation("合同编号")
    private String po_contract_code;

    /**
     * 项目编号
     */
    @TableField("project_code")
    @DataChangeLabelAnnotation("项目编号")
    private String project_code;

    /**
     * 采购合同ID
     */
    @TableField("po_contract_id")
    private Integer po_contract_id;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
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
     * 订单日期
     */
    @TableField("order_date")
    @DataChangeLabelAnnotation("订单日期")
    private LocalDateTime order_date;

    /**
     * 交货日期
     */
    @TableField("delivery_date")
    @DataChangeLabelAnnotation("交货日期")
    private LocalDateTime delivery_date;

    /**
     * 运输方式：1-公路；2-铁路；3-多式联运；
     */
    @TableField("delivery_type")
    @DataChangeLabelAnnotation("运输方式")
    private String delivery_type;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    @TableField("settle_type")
    @DataChangeLabelAnnotation("结算方式")
    private String settle_type;

    /**
     * 结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    @TableField("bill_type")
    @DataChangeLabelAnnotation("结算单据类型")
    private String bill_type;

    /**
     * 付款方式：1-银行转账
     */
    @TableField("payment_type")
    @DataChangeLabelAnnotation("付款方式")
    private String payment_type;

    /**
     * 交货地点
     */
    @TableField("delivery_location")
    @DataChangeLabelAnnotation("交货地点")
    private String delivery_location;

    /**
     * 备注
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("备注")
    private String remark;

    /**
     * 审批状态  0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    @DataChangeLabelAnnotation("审批状态")
    private String status;

    /**
     * 入库数量
     */
    @TableField("in_qty")
    @DataChangeLabelAnnotation("入库数量")
    private BigDecimal in_qty;

    /**
     * 已结算入库数量
     */
    @TableField("settled_in_qty")
    @DataChangeLabelAnnotation("已结算入库数量")
    private BigDecimal settled_in_qty;

    /**
     * 已作废入库数量
     */
    @TableField("canceled_in_qty")
    @DataChangeLabelAnnotation("已作废入库数量")
    private BigDecimal canceled_in_qty;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    @DataChangeLabelAnnotation("删除状态")
    private Boolean is_del;

    /**
     * 流程状态
     */
    @TableField(value="next_approve_name")
    @DataChangeLabelAnnotation("流程状态")
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
    @DataChangeLabelAnnotation("流程实例编号")
    private String bpm_instance_code;

    /**
     * 审批流程名称
     */
    @TableField(value="bpm_process_name")
    @DataChangeLabelAnnotation("审批流程名称")
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
    @DataChangeLabelAnnotation("作废流程实例编号")
    private String bpm_cancel_instance_code;

    /**
     * 作废 审批流程名称
     */
    @TableField(value="bpm_cancel_process_name")
    @DataChangeLabelAnnotation("作废审批流程名称")
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
    private Integer dbversion;
}
