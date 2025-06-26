package com.xinyirun.scm.bean.system.bo.business.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMessageBo implements Serializable {

    private static final long serialVersionUID = -8423730179015379207L;

    /**
     * 监管任务id, 入库单id, ...
     */
    private Integer serial_id;

    /**
     * 任务单号
     */
    private String serial_code;

    private String serial_type;

    /**
     * 监管任务状态
     */
    private String status;

    private String status_name;

    private String label;

    /**
     * 出库/提货时间
     */
    private LocalDateTime out_time;

    /**
     *入库/卸货时间
     */
    private LocalDateTime in_time;

    /**
     * 空车过磅创建时间
     */
    private LocalDateTime out_empty_time;

    /**
     * 正在装货创建时间
     */
    private LocalDateTime out_loading_time;

    /**
     * 重车出库创建时间
     */
    private LocalDateTime out_heavy_time;

    /**
     * 重车过磅创建时间
     */
    private LocalDateTime in_heavy_time;

    /**
     * 正在卸货创建时间
     */
    private LocalDateTime in_unloading_time;

    /**
     * 空车出库创建时间
     */
    private LocalDateTime in_empty_time;

    /**
     * 类型
     */
    private String type;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 货物库存
     */
    private String qty_avaible;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 商品规格
     */
    private String spec;

    /**
     * 监管任务损耗预警百分比
     */
    private Double m_monitor_loss_time;
}
