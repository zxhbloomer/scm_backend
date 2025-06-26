package com.xinyirun.scm.bean.entity.busniess.allocate;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_allocate")
public class BAllocateEntity implements Serializable {

    private static final long serialVersionUID = 623081067710824801L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 调拨单号
     */
    @TableField("code")
    private String code;

    /**
     * 调出货主id
     */
    @TableField("out_owner_id")
    private Integer out_owner_id;

    /**
     * 调出货主code
     */
    @TableField("out_owner_code")
    private String out_owner_code;

    /**
     * 调出委托方id
     */
    @TableField("out_consignor_id")
    private Integer out_consignor_id;

    /**
     * 调出委托方code
     */
    @TableField("out_consignor_code")
    private String out_consignor_code;

    /**
     * 调入货主id
     */
    @TableField("in_owner_id")
    private Integer in_owner_id;

    /**
     * 调入货主code
     */
    @TableField("in_owner_code")
    private String in_owner_code;

    /**
     * 调入委托方id
     */
    @TableField("in_consignor_id")
    private Integer in_consignor_id;

    /**
     * 调入委托方code
     */
    @TableField("in_consignor_code")
    private String in_consignor_code;

    /**
     * 调入仓库id
     */
    @TableField("in_warehouse_id")
    private Integer in_warehouse_id;

    /**
     * 调入仓库code
     */
    @TableField("in_warehouse_code")
    private String in_warehouse_code;

    /**
     * 调出仓库id
     */
    @TableField("out_warehouse_id")
    private Integer out_warehouse_id;

    /**
     * 调出仓库code
     */
    @TableField("out_warehouse_code")
    private String out_warehouse_code;

    /**
     * 订单id
     */
    @TableField(value="order_id")
    private Integer order_id;

    /**
     * 调拨日期
     */
    @TableField(value="allocate_time")
    private LocalDateTime allocate_time;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否自动生成入出库单 0否 1是
     */
    @TableField("auto")
    private Boolean auto;

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


}
