package com.xinyirun.scm.bean.system.vo.report.contract;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 采购合同统计表
 * @CreateTime : 2023/9/19 15:55
 */

@Data
public class PurchaseContractStatisticsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3924454522568599569L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 主键id 集合
     */
    private List<String> ids;


    /**
     * 采购合同号
     */
    private String contract_no;

    /**
     * 直属库名称
     */
    private String warehouse_name;

    /**
     * 直属库 ID
     */
    private Integer warehouse_id;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 物料编码
     */
    private String spec;

    /**
     * 合同数量
     */
    private BigDecimal contract_num;

    /**
     * 入库数量
     */
    private BigDecimal in_qty;

    /**
     * 当日出库数量
     */
    private BigDecimal today_out_qty;


    /**
     * 累计出库数量
     */
    private BigDecimal out_qty;


    /**
     * 开库日期
     */
    private String start_date;

    /**
     * 开库日期 开始时间
     */
    private String start_date_start;

    /**
     * 开库日期 结束时间
     */
    private String start_date_end;


    /**
     * 开库时间
     */
    private String start_time;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * 业务开始时间查询
     */
    private String batch;

}
