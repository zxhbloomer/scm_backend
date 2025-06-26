package com.xinyirun.scm.bean.entity.busniess.adjust;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调整明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_adjust_detail")
@DataChangeEntityAnnotation(value="库存调整子表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.DataChangeStrategyBAdjustDetailServiceImpl")
public class BAdjustDetailEntity implements Serializable {


    private static final long serialVersionUID = -75002899270786045L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @DataChangeLabelAnnotation("id")
    private Integer id;

    /**
     * 单号
     */
    @TableField("code")
    @DataChangeLabelAnnotation("库存调整子表编号")
    private String code;

    /**
     * 单据状态
     */
    @TableField("status")
    @DataChangeLabelAnnotation("单据状态：补充一下内容")
    private String status;

    /**
     * 调整单id
     */
    @TableField("adjust_id")
    private Integer adjust_id;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    @DataChangeLabelAnnotation(value = "仓库id", extension = "getWarehouseNameExtension")
    private Integer warehouse_id;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 物料id
     */
    @TableField("sku_id")
    @DataChangeLabelAnnotation("物料id")
    private Integer sku_id;

    /**
     * 物料id
     */
    @TableField("sku_code")
    @DataChangeLabelAnnotation("sku_code")
    private String sku_code;

    /**
     * 原库存数量
     */
    @TableField("qty")
    @DataChangeLabelAnnotation("原库存数量")
    private BigDecimal qty;

    /**
     * 调整库存数量
     */
    @TableField("qty_adjust")
    @DataChangeLabelAnnotation("调整库存数量")
    private BigDecimal qty_adjust;

    /**
     * 调整差量
     */
    @TableField("qty_diff")
    @DataChangeLabelAnnotation("调整库存的差量")
    private BigDecimal qty_diff;

    /**
     * 调整后平均单价
     */
    @TableField("adjusted_price")
    @DataChangeLabelAnnotation("调整后平均单价")
    private BigDecimal adjusted_price;

    /**
     * 调整后货值
     */
    @TableField("adjusted_amount")
    @DataChangeLabelAnnotation("调整后货值")
    private BigDecimal adjusted_amount;

    /**
     * 库存调整规则
     */
    @TableField("adjusted_rule")
    @DataChangeLabelAnnotation("库存调整规则：补充")
    private String adjusted_rule;

    /**
     * 库存流水id
     */
    @TableField("inventory_account_id")
    private Integer inventory_account_id;

    /**
     * 审核人id
     */
    @TableField("e_id")
    private Integer e_id;

    /**
     * 审核时间
     */
    @TableField("e_dt")
    @DataChangeLabelAnnotation("审核时间")
    private LocalDateTime e_dt;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建人id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人名称", fixed = true, extension = "getUserNameExtension")
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


}
