package com.xinyirun.scm.bean.entity.business.so.soorder;

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
 * 销售订单表
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_order")
public class BSoOrderEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3555505017977494453L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号，自动生成
     */
    @TableField("code")
    @DataChangeLabelAnnotation("订单编号")
    private String code;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    @DataChangeLabelAnnotation("销售合同编号")
    private String so_contract_code;

    /**
     * 项目编号
     */
    @TableField("project_code")
    @DataChangeLabelAnnotation("项目编号")
    private String project_code;

    /**
     * 销售合同ID
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Integer customer_id;

    /**
     * 客户编码
     */
    @TableField("customer_code")
    @DataChangeLabelAnnotation("客户编码")
    private String customer_code;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    @DataChangeLabelAnnotation("客户名称")
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
    @DataChangeLabelAnnotation("销售方编码")
    private String seller_code;

    /**
     * 销售方名称
     */
    @TableField("seller_name")
    @DataChangeLabelAnnotation("销售方名称")
    private String seller_name;

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
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    @DataChangeLabelAnnotation("审批状态")
    private String status;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    @DataChangeLabelAnnotation("删除标记")
    private Boolean is_del;

    /**
     * 流程状态
     */
    @TableField(value="next_approve_name")
    @DataChangeLabelAnnotation("流程状态")
    private String next_approve_name;

    /**
     * 流程实例ID
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
     * 审批流程名称：销售订单新增审批
     */
    @TableField(value="bpm_process_name")
    @DataChangeLabelAnnotation("审批流程名称")
    private String bpm_process_name;

    /**
     * 作废流程实例ID
     */
    @TableField(value="bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * 作废流程实例code
     */
    @TableField(value="bpm_cancel_instance_code")
    @DataChangeLabelAnnotation("作废流程实例编号")
    private String bpm_cancel_instance_code;

    /**
     * 作废审批流程名称：作废审批流程
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