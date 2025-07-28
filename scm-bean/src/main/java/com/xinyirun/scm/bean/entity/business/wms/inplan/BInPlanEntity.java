package com.xinyirun.scm.bean.entity.business.wms.inplan;

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
 * 入库计划
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_in_plan")
public class BInPlanEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8231630392927790144L;

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
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 计划时间
     */
    @TableField("plan_time")
    private LocalDateTime plan_time;

    /**
     * 超收比例
     */
    @TableField("over_receipt_rate")
    private BigDecimal over_receipt_rate;

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
     * 委托方id
     */
    @TableField("consignor_id")
    private Integer consignor_id;

    /**
     * 委托方编码
     */
    @TableField("consignor_code")
    private String consignor_code;

    /**
     * 是否删除
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
     * 下一个审批人姓名
     */
    @TableField("next_approve_name")
    private String next_approve_name;    /**
     * BPM实例id
     */
    @TableField("bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * BPM实例编码
     */
    @TableField("bpm_instance_code")
    private String bpm_instance_code;

    /**
     * BPM流程名称
     */
    @TableField("bpm_process_name")
    private String bpm_process_name;    /**
     * BPM取消实例id
     */
    @TableField("bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * BPM取消实例编码
     */
    @TableField("bpm_cancel_instance_code")
    private String bpm_cancel_instance_code;

    /**
     * BPM取消流程名称
     */
    @TableField("bpm_cancel_process_name")
    private String bpm_cancel_process_name;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

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
}
