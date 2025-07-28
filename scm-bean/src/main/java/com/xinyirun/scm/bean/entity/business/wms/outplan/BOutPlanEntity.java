package com.xinyirun.scm.bean.entity.business.wms.outplan;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划
 * </p>
 *
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_out_plan")
public class BOutPlanEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8231630392927790146L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 出库计划单号
     */
    @TableField("code")
    private String code;

    /**
     * 出库类型：0=销售出库 1=调拨出库 2=退货出库 3=监管出库 4=普通出库 5=生产出库
     */
    @TableField("type")
    private String type;

    /**
     * 出库状态：0制单，1已提交，2审核通过，3审核驳回，4已出库，5作废
     */
    @TableField("status")
    private String status;

    /**
     * 计划时间
     */
    @TableField("plan_time")
    private LocalDateTime plan_time;

    /**
     * 超发比例
     */
    @TableField("over_delivery_rate")
    private BigDecimal over_delivery_rate;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主编码
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 收货方id
     */
    @TableField("consignee_id")
    private Integer consignee_id;

    /**
     * 收货方编码
     */
    @TableField("consignee_code")
    private String consignee_code;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableField("is_del")
    @TableLogic
    private Integer is_del;

    /**
     * 是否ERP模式
     */
    @TableField("is_erp_model")
    private Boolean is_erp_model;

    /**
     * 下一审批人姓名
     */
    @TableField("next_approve_name")
    private String next_approve_name;

    /**
     * bpm实例id
     */
    @TableField("bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * bpm实例编码
     */
    @TableField("bpm_instance_code")
    private String bpm_instance_code;

    /**
     * bpm流程名称
     */
    @TableField("bpm_process_name")
    private String bpm_process_name;

    /**
     * bpm作废实例id
     */
    @TableField("bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * bpm作废实例编码
     */
    @TableField("bpm_cancel_instance_code")
    private String bpm_cancel_instance_code;

    /**
     * bpm作废流程名称
     */
    @TableField("bpm_cancel_process_name")
    private String bpm_cancel_process_name;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

}