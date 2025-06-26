package com.xinyirun.scm.bean.system.vo.report.ledger;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 稻壳 出库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
public class ProcessingComboWarehouseOutProgressVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -8869951051975698472L;

    /**
     * 分页熟路
     */
    private PageCondition pageCondition;

    /**
     * 品种
     */
    private String goods_name;

    /**
     * 发货单位  监管任务关联的销售合同的销售组织主体, 全称
     */
    private String out_owner_name;

    /**
     * 发货地
     */
    private String out_warehouse_name;

    /**
     * 收货单位（流向）
     */
    private String in_warehouse_name;

    /**
     * 承运商
     */
    private String customer_name;

    /**
     * 收货单位 id
     */
    private Integer in_warehouse_id;

    /**
     * 采购/销售合同
     */
    private String contract_no;

    /**
     * 出库时间
     */
    private LocalDateTime out_time;

    /**
     * 出库时间 开始
     */
    private LocalDateTime out_time_start;


    /**
     * 出库时间 结束
     */
    private LocalDateTime out_time_end;


    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 净重
     */
    private BigDecimal qty;

    /**
     * 糙米使用量
     */
    private BigDecimal ricehull_qty;

    /**
     * 玉米使用量
     */
    private BigDecimal maize_qty;

    /**
     * 稻谷使用量
     */
    private BigDecimal rice_qty;

    /**
     * 小麦使用量
     */
    private BigDecimal wheat_qty;

    /**
     * id 集合
     */
    private Integer[] ids;

    /**
     * id
     */
    private Integer id;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * 扣减退货的真实数量
     */
    private BigDecimal actual_count_return;

    /**
     * 退货数量
     */
    private BigDecimal return_qty;
}
