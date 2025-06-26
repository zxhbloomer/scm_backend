package com.xinyirun.scm.bean.system.vo.business.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Wang Qianfeng
 * @date 2022/9/1 9:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BQtyLossScheduleReportVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -4163751099513031173L;

    /**
     * 审核状态
     */
    private String audit_status_name;

    /**
     * 物流单号
     */
    private String schedule_code;

    /**
     * 损耗数量
     */
    private BigDecimal qty_loss;

    /**
     * 规格
     */
    private String spec;

    /**
     * 品名
     */
    private String pm;

    /**
     * 承运商
     */
    private String customer_name;

    private String id_str;
    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    private Integer id;

    /**
     * 物料名称
     */
    private String sku_name;
    private String sku_code;

    private String goods_code;

    /**
     * 任务单号
     */
    private String code;

    /**
     * 监管任务状态
     */
    private String status_name;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 已收货数量
     */
    private BigDecimal in_qty;

    /**
     * 已发货数量
     */
    private BigDecimal out_qty;

    /**
     * 1 损耗， 2在途
     */
    private Integer query_type;

    /**
     * 车次
     */
    private Integer num;

    /**
     * 员工ID
     */
    private Long staff_id;

    /**
     * 当前日期
     */
    private String date;

    /**
     * 当前日期
     */
    private String batch;

    /**
     * 商品属性
     */
    private String prop_name;

    /**
     * 类型
     */
    private String type;

    /**
     * 派车数
     */
    private Integer counts;

    /**
     * 创建 时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 出库仓库
     */
    private String out_warehouse_name;

    /**
     * 入库仓库
     */
    private String in_warehouse_name;

    /**
     * 原粮出库数量 ，取值采购合同关联的，且审批通过时间是当天的，且仓库类型是直属库的出库单.出库数量(换算前)
     */
    private BigDecimal out_raw_grain_count;

    /**
     * 在途数量
     */
    private BigDecimal out_way_qty;

    /**
     * 在途数量合计
     */
    private BigDecimal out_sum_way_qty;
}
