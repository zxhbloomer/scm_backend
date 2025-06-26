package com.xinyirun.scm.bean.system.vo.business.rpd;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000128Vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/5/16 15:46
 */
@Data
public class BProductDailyVo extends BaseVo implements Serializable {
    private static final long serialVersionUID = 4462401617554965059L;

    /**
     * 开始执行时间
     */
    private String init_time;

    /**
     * 结束执行时间
     */
    private String end_time;

    /**
     * 定时任务执行时间, t-1
     */
    private LocalDateTime date;

    /**
     * 1, 走 url, 2 作废
     */
    private String type;

    /**
     * 仓库 code
     */
    private String warehouse_code;

    /**
     * 掺混/加工库点
     */
    private String warehouse_name;

    private Integer warehouse_id;

    private List<Integer> warehouse_ids;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    private String id;

    private List<String> ids;

    /**
     * 生产方式
     */
    private String product_type;

    /**
     * 稻谷 定向入库
     */
    private BigDecimal a_in_qty;

    /**
     * 稻谷 掺混/加工使用
     */
    private BigDecimal a_product_qty;

    /**
     * 稻谷 出库数量
     */
    private BigDecimal a_out_qty;

    /**
     * 稻谷 库存数量
     */
    private BigDecimal a_inventory_qty;

    /**
     * 糙米 加工入库
     */
    private BigDecimal b_in_qty;

    /**
     * 糙米 掺混数量
     */
    private BigDecimal b_cost_qty;

    /**
     * 糙米 出库数量
     */
    private BigDecimal b_out_qty;

    /**
     * 糙米 库存数量
     */
    private BigDecimal b_inventory_qty;

    /**
     * 玉米 入库数量
     */
    private BigDecimal c_in_qty;

    /**
     * 玉米 掺混使用
     */
    private BigDecimal c_cost_qty;

    /**
     * 玉米 库存数量
     */
    private BigDecimal c_inventory_qty;

    /**
     * 玉米 配比
     */
    private BigDecimal router;

    /**
     * 混合物 入库数量
     */
    private BigDecimal d_in_qty;

    /**
     * 混合物 出库数量
     */
    private BigDecimal d_out_qty;

    /**
     * 混合物 损耗
     */
    private BigDecimal loss_qty;

    /**
     * 混合物 剩余库存
     */
    private BigDecimal d_residue_qty;

    /**
     * 稻壳 入库数量
     */
    private BigDecimal e_in_qty;

    /**
     * 稻壳 出库数量
     */
    private BigDecimal e_out_qty;

    /**
     * 剩余库存
     */
    private BigDecimal e_residue_qty;

    /**
     * 每日加工报表, 配置
     */
    private P00000128Vo p00000128Vo;

    /**
     * 业务启动日期
     */
    private String batch;

}
