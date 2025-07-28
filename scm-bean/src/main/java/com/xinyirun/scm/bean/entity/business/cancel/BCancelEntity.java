package com.xinyirun.scm.bean.entity.business.cancel;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作废单
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_cancel")
public class BCancelEntity implements Serializable {

    private static final long serialVersionUID = 6139888360833088132L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务id（入库单id、出库单id）
     */
    @TableField("business_id")
    private Integer business_id;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String business_type;

    /**
     * 业务单号（入库单code、出库单code）
     */
    @TableField("business_code")
    private String business_code;

    /**
     * 状态 1-影响库存的数据 0-不影响库存
     */
    @TableField("status")
    private String status;

    /**
     * 数量（入库单入库数量、出库单出库数量）
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 时间（入库单入库时间、出库单出库时间）
     */
    @TableField("time")
    private LocalDateTime time;

    /**
     * 作废时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancel_time;

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
     * 入库库位id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 入库库位id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 入库库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编码
     */
    @TableField("sku_code")
    private String sku_code;

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
     * 备注
     */
    @TableField("remark")
    private String remark;
}
