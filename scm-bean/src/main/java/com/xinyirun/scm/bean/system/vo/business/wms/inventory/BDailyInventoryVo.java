package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 每日库存表
 * </p>
 *
 * @author wwl
 * @since 2022-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDailyInventoryVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 2356243241168713170L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 时间
     */
    private LocalDateTime dt;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;


    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 委托方编码
     */
    private String consignor_code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;
    private Integer[] warehouse_ids;
    private String[] warehouse_types;
    private String warehouse_type_name;

    /**
     * 仓库
     */
    private String warehouse_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区
     */
    private String location_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位
     */
    private String bin_name;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 规格
     */
    private String sku_name;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 库存数量
     */
    private BigDecimal qty;

    /**
     * 入库量
     */
    private BigDecimal qty_in;

    /**
     * 出库量
     */
    private BigDecimal qty_out;

    /**
     * 调整数量
     */
    private BigDecimal qty_adjust;

    /**
     * 变动量
     */
    private BigDecimal qty_diff;

    /**
     * 调整量
     */
    private BigDecimal actual_weight;

    /**
     * 移动加权货值单价
     */
    private BigDecimal price;

    /**
     * 库存货值
     */
    private BigDecimal inventory_amount;

    /**
     * 实时单价
     */
    private BigDecimal realtime_price;

    /**
     * 实时货值
     */
    private BigDecimal realtime_amount;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 业务板块
     */
    private String business_type_name;

    /**
     * 行业
     */
    private String industry_name;

    /**
     * 类别
     */
    private String category_name;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 商品属性
     */
    private String goods_prop;

    private Integer[] owner_ids;
}
