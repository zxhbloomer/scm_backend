package com.xinyirun.scm.bean.entity.busniess.so.socontract;

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
 * 销售合同表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_contract")
public class BSoContractEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3307906718066521308L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号自动生成编号
     */
    @TableField("code")
    private String code;

    /**
     * 类型：0：标准合同；1：框架合同
     */
    @TableField("type")
    private String type;

    /**
     * 合同编号，为用户手写编号，如果页面未输入，等于code自动编号
     */
    @TableField("contract_code")
    private String contract_code;

    /**
     * 项目编号
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 审批状态  0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    @TableField("status")
    private String status;

    /**
     * 供应商id
     */
    @TableField("supplier_id")
    private Integer supplier_id;

    /**
     * 购买方id
     */
    @TableField("purchaser_id")
    private Integer purchaser_id;

    /**
     * 签约日期
     */
    @TableField("sign_date")
    private LocalDateTime sign_date;

    /**
     * 到期日期
     */
    @TableField("expiry_date")
    private LocalDateTime expiry_date;

    /**
     * 交货日期
     */
    @TableField("delivery_date")
    private LocalDateTime delivery_date;

    /**
     * 运输方式：1-公路；2-铁路；3-多式联运；
     */
    @TableField("delivery_type")
    private String delivery_type;

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
     * 交货地点
     */
    @TableField("delivery_location")
    private String delivery_location;

    /**
     * 审批后自动生成订单：默认true 
     */
    @TableField("auto_create_order")
    private String auto_create_order;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


    /**
     * 合同总金额
     */
    @TableField("contract_amount_sum")
    private BigDecimal contract_amount_sum;


    /**
     * 总销售数量
     */
    @TableField("contract_total")
    private BigDecimal contract_total;


    /**
     * 总税额
     */
    @TableField("tax_amount_sum")
    private BigDecimal tax_amount_sum;

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
    @TableField("dbversion")
    private Integer dbversion;


}
