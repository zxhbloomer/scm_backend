package com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售货权转移主表实体类
 * 
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_cargo_right_transfer")
public class BSoCargoRightTransferEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1763083828674328539L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @DataChangeLabelAnnotation("主键ID")
    private Integer id;

    /**
     * 货权转移单号
     */
    @TableField("code")
    @DataChangeLabelAnnotation("货权转移单号")
    private String code;

    /**
     * 货权转移日期
     */
    @TableField("transfer_date")
    @DataChangeLabelAnnotation("货权转移日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime transfer_date;

    /**
     * 状态(0-待审批,1-审批中,2-执行中,3-驳回,4-作废审批中,5-已作废,6-已完成)
     */
    @TableField("status")
    @DataChangeLabelAnnotation("状态")
    private String status;

    /**
     * 销售订单ID
     */
    @TableField("so_order_id")
    @DataChangeLabelAnnotation("销售订单ID")
    private Integer so_order_id;

    /**
     * 销售订单号
     */
    @TableField("so_order_code")
    @DataChangeLabelAnnotation("销售订单号")
    private String so_order_code;

    /**
     * 销售合同ID
     */
    @TableField("so_contract_id")
    @DataChangeLabelAnnotation("销售合同ID")
    private Integer so_contract_id;

    /**
     * 销售合同号
     */
    @TableField("so_contract_code")
    @DataChangeLabelAnnotation("销售合同号")
    private String so_contract_code;

    /**
     * 项目编码
     */
    @TableField("project_code")
    @DataChangeLabelAnnotation("项目编码")
    private String project_code;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    @DataChangeLabelAnnotation("客户ID")
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
     * 销售员ID
     */
    @TableField("seller_id")
    @DataChangeLabelAnnotation("销售员ID")
    private Integer seller_id;

    /**
     * 销售员编码
     */
    @TableField("seller_code")
    @DataChangeLabelAnnotation("销售员编码")
    private String seller_code;

    /**
     * 销售员姓名
     */
    @TableField("seller_name")
    @DataChangeLabelAnnotation("销售员姓名")
    private String seller_name;

    /**
     * 货权转移地点
     */
    @TableField("transfer_location")
    @DataChangeLabelAnnotation("货权转移地点")
    private String transfer_location;

    /**
     * 备注
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("备注")
    private String remark;

    /**
     * 转移总数量
     */
    @TableField("total_qty")
    @DataChangeLabelAnnotation("转移总数量")
    private BigDecimal total_qty;

    /**
     * 转移总金额
     */
    @TableField("total_amount")
    @DataChangeLabelAnnotation("转移总金额")
    private BigDecimal total_amount;

    /**
     * 下一审批人
     */
    @TableField("next_approve_name")
    @DataChangeLabelAnnotation("下一审批人")
    private String next_approve_name;

    /**
     * BPM实例ID
     */
    @TableField("bpm_instance_id")
    @DataChangeLabelAnnotation("BPM实例ID")
    private Integer bpm_instance_id;

    /**
     * BPM实例编码
     */
    @TableField("bpm_instance_code")
    @DataChangeLabelAnnotation("BPM实例编码")
    private String bpm_instance_code;

    /**
     * BPM流程名称
     */
    @TableField("bpm_process_name")
    @DataChangeLabelAnnotation("BPM流程名称")
    private String bpm_process_name;

    /**
     * BPM取消实例ID
     */
    @TableField("bpm_cancel_instance_id")
    @DataChangeLabelAnnotation("BPM取消实例ID")
    private Integer bpm_cancel_instance_id;

    /**
     * BPM取消实例编码
     */
    @TableField("bpm_cancel_instance_code")
    @DataChangeLabelAnnotation("BPM取消实例编码")
    private String bpm_cancel_instance_code;

    /**
     * BPM取消流程名称
     */
    @TableField("bpm_cancel_process_name")
    @DataChangeLabelAnnotation("BPM取消流程名称")
    private String bpm_cancel_process_name;

    /**
     * 是否删除(0-否,1-是)
     */
    @TableField("is_del")
    @DataChangeLabelAnnotation("是否删除")
    @TableLogic
    private Boolean is_del;

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