package com.xinyirun.scm.bean.entity.busniess.out;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_out_plan")
public class BOutPlanEntity implements Serializable {

    private static final long serialVersionUID = -4947548181518171543L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 委托单号
     */
    @TableField("code")
    private String code;

    /**
     * 外部系统单号
     */
    @TableField("extra_code")
    private String extra_code;

    /**
     * 放货指令编号
     */
    @TableField("release_order_code")
    private String release_order_code;

    /**
     * 是否调度状态
     */
    @TableField("schedule_status")
    private String schedule_status;

    /**
     * 委托时间
     */
    @TableField("plan_time")
    private LocalDateTime plan_time;

    /**
     * 数据来源:  0:文件导入；1: 人工添加；2:平台推送;3担保存货
     */
    @TableField("data_sources")
    private Boolean data_sources;

    /**
     * 作废原因
     */
    @TableField("invalid_reason")
    private String invalid_reason;

    /**
     * 作废原因id
     */
    @TableField("invalid_reason_id")
    private Integer invalid_reason_id;

    /**
     * 责任归属：0 我方1 客户
     */
    @TableField("responsibility_owner")
    private Boolean responsibility_owner;

    /**
     * 情况说明
     */
    @TableField("information_note")
    private String information_note;

    /**
     * 是否同意:0否;1是
     */
    @TableField("is_agree")
    private Boolean is_agree;

    /**
     * 出库类型
     */
    @TableField("type")
    private String type;

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
     * 中止json
     */
    @TableField("discontinue_json")
    private String discontinue_json;

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
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
