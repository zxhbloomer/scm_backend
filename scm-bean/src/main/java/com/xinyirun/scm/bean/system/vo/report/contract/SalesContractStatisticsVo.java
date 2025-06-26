package com.xinyirun.scm.bean.system.vo.report.contract;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 采购合同统计表
 * @CreateTime : 2023/9/19 15:55
 */

@Data
public class SalesContractStatisticsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3924454522568599569L;

    private Integer no;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 物流订单数
     */
    private Integer schedule_count;

    /**
     * 状态
     */
    private String status_name;

    /**
     * 合同日期
     */
    private LocalDate contract_dt;

    /**
     * 到期日期
     */
    private LocalDate contract_expire_dt;

    /**
     * 客户
     */
    private String client_name;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 集合里的数据
     */
    private List<JSONObject> json_objects;

    /**
     * 执行情况
     */
    private String execute_processing;

    /**
     * 出库地
     */
    private String out_address;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * 合计 已出库数量
     */
    private BigDecimal has_handle_count;

    /**
     * 合计在途数量
     */
    private BigDecimal in_transit_count;

    /**
     * 合计 到货数量
     */
    private BigDecimal arrived_count;

    /**
     * 合计损耗数量
     */
    private BigDecimal qty_loss;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * id 集合
     */
    private Integer[] ids;
}
