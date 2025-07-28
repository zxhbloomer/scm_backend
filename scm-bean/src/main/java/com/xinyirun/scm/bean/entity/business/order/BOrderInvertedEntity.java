package com.xinyirun.scm.bean.entity.business.order;
import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 倒排表数据快照
 * </p>
 *
 * @author xinyirun
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_order_inverted")
public class BOrderInvertedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -623637902071005983L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 竞拍日期
     */
    @TableField("auction_date")
    private String auction_date;

    /**
     * 开库日期
     */
    @TableField("opening_date")
    private LocalDateTime opening_date;

    /**
     * 采购合同号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 出库到期日
     */
    @TableField("delivery_due_date")
    private LocalDateTime delivery_due_date;

    /**
     * 实际储存库点
     */
    @TableField("warehouse_name")
    private String warehouse_name;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 合同量
     */
    @TableField("contract_quantity")
    private BigDecimal contract_quantity;

    /**
     * 实际应出库数量（扣除升贴水数量）
     */
    @TableField("actual_quantity")
    private BigDecimal actual_quantity;

    /**
     * 剩余数量
     */
    @TableField("remaining_quantity")
    private BigDecimal remaining_quantity;

    /**
     * 实际日出库量
     */
    @TableField("actual_daily_quantity")
    private BigDecimal actual_daily_quantity;

    /**
     * 累计出库量
     */
    @TableField("accumulated_out_quantity")
    private BigDecimal accumulated_out_quantity;

    /**
     * 计划出库天数
     */
    @TableField("plan_out_days")
    private String plan_out_days;

    /**
     * 实际出库耗用天数
     */
    @TableField("actual_plan_out_days")
    private String actual_plan_out_days;

    /**
     * 日出库计划
     */
    @TableField("plan_out_day")
    private BigDecimal plan_out_day;

    /**
     * 出库进度
     */
    @TableField("plan_out_speed")
    private BigDecimal plan_out_speed;

    /**
     * 备份日期（记录前一天时间）
     */
    @TableField("backups_date")
    private LocalDateTime backups_date;

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
