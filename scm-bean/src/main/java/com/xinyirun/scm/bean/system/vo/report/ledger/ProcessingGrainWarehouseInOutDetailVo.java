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
public class ProcessingGrainWarehouseInOutDetailVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -4146615737700332536L;

    /**
     * 分页熟路
     */
    private PageCondition pageCondition;

    /**
     * 品种
     */
    private String goods_name;

    /**
     * 收货单位, 发货单位
     */
    private String warehouse_name;

    /**
     * 仓库 id
     */
    private Integer warehouse_id;

    /**
     * 物流运输合同号
     */
    private String waybill_code;

    /**
     * 出库时间, 入库时间
     */
    private LocalDateTime time;

    /**
     * 出库时间, 入库时间 开始
     */
    private LocalDateTime time_start;

    /**
     * 出库时间, 入库时间 结束
     */
    private LocalDateTime time_end;


    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 净重
     */
    private BigDecimal qty;

    /**
     * 主键 ids
     */
    private Integer[] ids;

    /**
     * 主键 id
     */
    private Integer id;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * 发货单位
     */
    private String out_warehouse_name;
}
