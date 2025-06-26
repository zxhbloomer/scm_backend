package com.xinyirun.scm.bean.system.vo.report.ledger;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 加工稻谷, 小麦入库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
public class ProcessingMaizeAndWheatWarehouseInProgressVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6407365645665690190L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 主键id集合
     */
    private Integer[] ids;

    /**
     * 分页熟路
     */
    private PageCondition pageCondition;

    /**
     * 发货单位
     */
    private String supplier_name;

    /**
     * 采购合同号
     */
    private String contract_no;

    /**
     * 品种
     */
    private String goods_name;

    /**
     * 入库时间
     */
    private LocalDateTime e_dt;

    /**
     * 入库时间 开始
     */
    private LocalDateTime e_dt_start;

    /**
     * 入库时间 结束
     */
    private LocalDateTime e_dt_end;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 入库净重
     */
    private BigDecimal actual_count;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * 发货地
     */
    private String out_warehouse_name;

    /**
     * 收货地
     */
    private String in_warehouse_name;

}
