package com.xinyirun.scm.bean.entity.busniess.pp;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 生产计划表
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_pp")
public class BPpEntity implements Serializable {

    
    private static final long serialVersionUID = 2134888486446659100L;

    /**
     * 主键
     */
    @TableId("id")
    private Integer id;

    /**
     * 业务单号
     */
    @TableField("code")
    private String code;

    /**
     * 状态：0制单，1已提交，2审核通过，3审核驳回，4作废，5作废审核中，6已完成
     */
    @TableField("status")
    private String status;

    /**
     * 上一个状态：0制单，1已提交，2审核通过，3审核驳回，4作废，5作废审核中，6已完成
     */
    @TableField("pre_status")
    private String pre_status;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主code
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     *配方id
     */
    @TableField("router_id")
    private Integer router_id;

    /**
     *配方code
     */
    @TableField("router_code")
    private String router_code;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库区code
     */
    @TableField("location_code")
    private String location_code;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 库位code
     */
    @TableField("bin_code")
    private String bin_code;

    /**
     * 计划开始入库时间
     */
    @TableField("plan_time")
    private LocalDateTime plan_time;

    /**
     * 计划结束入库时间
     */
    @TableField("plan_end_time")
    private LocalDateTime plan_end_time;

    /**
     * 放货指令id
     */
    @TableField("release_order_id")
    private Integer release_order_id;

    /**
     * 放货指令code
     */
    @TableField("release_order_code")
    private String release_order_code;

    /**
     *  放货指令详细表ID
     */
    @TableField("release_order_detail_id")
    private Integer release_order_detail_id;

    /**
     * 审核人id
     */
    @TableField("audit_id")
    private Integer audit_id;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime audit_time;

    /**
     * 作废审核人id
     */
    @TableField("cancel_audit_id")
    private Integer cancel_audit_id;

    /**
     * 作废审核时间
     */
    @TableField("cancel_audit_time")
    private LocalDateTime cancel_audit_time;

    /**
     * 原材料json
     */
    @TableField("json_material_list")
    private String json_material_list;

    /**
     * 产成品、副产品json
     */
    @TableField("json_product_list")
    private String json_product_list;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT)
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


}
