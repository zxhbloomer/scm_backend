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
public class ProcessingRiceHullWarehouseOutDetailVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -9015214114426967371L;

    /**
     * 分页熟路
     */
    private PageCondition pageCondition;

    /**
     * 品种
     */
    private String goods_name;


    /**
     * 收货单位
     */
    private String client_name;

    /**
     * 销售合同号
     */
    private String contract_no;


    /**
     * 出库时间
     */
    private LocalDateTime e_dt;

    /**
     * 出库时间 开始
     */
    private LocalDateTime e_dt_start;

    /**
     * 出库时间 结束
     */
    private LocalDateTime e_dt_end;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 净重
     */
    private BigDecimal actual_count;

    /**
     * 主键集合
     */
    private Integer[] ids;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 業務啓動日期
     */
    private String batch;
}
