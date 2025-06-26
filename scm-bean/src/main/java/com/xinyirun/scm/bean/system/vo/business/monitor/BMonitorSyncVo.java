package com.xinyirun.scm.bean.system.vo.business.monitor;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Wqf
 * @Description: 同步 业务中台VO
 * @CreateTime : 2023/5/10 14:16
 */

@Data
public class BMonitorSyncVo implements Serializable {

    private static final long serialVersionUID = 6369982009183331315L;

    /**
     * 监管任务 ID
     */
    private Integer id;

    /**
     * 监管任务 code
     */
    private String code;

    /**
     * 监管任务.车牌号
     */
    private String vehicle_no;

    /**
     * 监管任务.发货地仓库编号
     */
    private String out_warehouse_code;

    /**
     * 监管任务.发货地仓库名称
     */
    private String out_warehouse_name;


    /**
     * 监管任务.发货地仓库简称
     */
    private String out_warehouse_simple_name;

    /**
     * 监管任务.收货地仓库编号
     */
    private String in_warehouse_code;

    /**
     * 监管任务.收货地仓库名称
     */
    private String in_warehouse_name;

    /**
     * 监管任务.收货地仓库简称
     */
    private String in_warehouse_simple_name;

    /**
     * 监管任务.商品编号
     */
    private String goods_code;

    /**
     * 监管任务.商品名称
     */
    private String goods_name;

    /**
     * 监管任务.商品规格编号
     */
    private String sku_code;

    /**
     * 监管任务.商品规格名称
     */
    private String sku_name;

    /**
     * 监管任务.状态编码
     */
    private String status;

    /**
     * 监管任务.状态名称
     */
    private String status_name;

    /**
     * 监管任务.审核状态编码
     */
    private String audit_status;

    /**
     * 监管任务.审核状态名称
     */
    private String audit_status_name;


    /**
     * 监管任务.发货数量
     */
    private BigDecimal qty_out;

    /**
     * 监管任务.收货数量
     */
    private BigDecimal qty_in;

    /**
     * 监管任务.损耗
     */
    private BigDecimal qty_loss;

}
