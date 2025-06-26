package com.xinyirun.scm.bean.system.vo.report.ledger;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 加工稻谷入库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
public class ProcessingRiceWarehouseInProgressVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6407365645665690190L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 分页熟路
     */
    private PageCondition pageCondition;

    /**
     * 导出序号
     */
    private Integer no;

    /**
     * 实际存储库点
     */
    private String out_warehouse_name;

    /**
     * 实际存储库点 id
     */
    private Integer out_warehouse_id;

    /**
     * 品种
     */
    private String goods_name;

    /**
     * 所在货位混合扦样等级
     */
    private String spec;

    /**
     * 合同号
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
     * 出库净重（吨
     */
    private BigDecimal out_qty;

    /**
     * 加工掺混点
     */
    private String in_warehouse_name;

    /**
     * 加工掺混点 id
     */
    private Integer in_warehouse_id;

    /**
     * 入库时间
     */
    private LocalDateTime in_time;

    /**
     * 入库时间 开始
     */
    private LocalDateTime in_time_start;

    /**
     * 入库时间 结束
     */
    private LocalDateTime in_time_end;

    /**
     * 入库净重（吨）
     */
    private BigDecimal in_qty;

    /**
     * 导出 id
     */
    private Integer[] ids;

    /**
     * 业务启动日期
     */
    private String batch;


}
