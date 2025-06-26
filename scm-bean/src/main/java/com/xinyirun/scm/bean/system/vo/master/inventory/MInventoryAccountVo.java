package com.xinyirun.scm.bean.system.vo.master.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

// import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 库存流水
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存流水", description = "库存流水")
public class MInventoryAccountVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 2724501020238489841L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 业务编号
     */
    private String business_code;

    /**
     * '类型 0 入  1 出  2 调整'
     */
    private String type;

    /**
     * 流水类型
     *     IN_CREATE("10", "入库单生成"),                    // 数量进入锁定库存
     *     IN_AGREE("11", "入库单审核同意"),                 // 锁定库存转入可用库存，锁定库存释放
     *     IN_NOT_CANCEL("12", "入库单审核驳回"),            // 锁定库存释放
     *     IN_CANCEL("13", "入库单作废"),                   // 制单时：锁定时库存释放，审核通过时可用库存释放
     *     OUT_CREATE("20", "出库单生成"),
     *     OUT_AGREE("21", "出库单生成审核同意"),
     *     OUT_NOT_AGREE("22", "出库单审核驳回"),
     *     OUT_CANCEL("23", "出库单作废"),
     *     ADJUST_CREATE("30", "调整单生成"),
     *     ADJUST_AGREE("31", "调整单审核同意"),
     *     ADJUST_NOT_AGREE("32", "调整单审核驳回"),
     *     ADJUST_CANCELLED("33", "调整单作废"),
     */
    private String business_type;
    private String[] business_types;

    private String business_type_name;

    /**
     * 货主名称
     */
    private String owner_name;
    private String owner_id;
    private Integer[] owner_ids;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 物料编号
     */
    private String sku_code;

    /**
     * 物料名称
     */
    private String sku_name;

    /**
     * 板块
     */
    private String business_name;

    /**
     * 行业
     */
    private String industry_name;

    /**
     * 类别
     */
    private String category_name;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格
     */
    private String spec;

    /**
     * 业务类型
     */
    private String serial_type_name;
    private String serial_type;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库名称
     */
    private Integer warehouse_id;
    private Integer[] warehouse_ids;
    private String[] warehouse_types;
    private String warehouse_type_name;

    /**
     * 数量
     */
    private String qty;

    /**
     * 库存余额
     */
    private String qty_inventory_total;

    /**
     * 库存余额
     */
    private String qty_inventory;

    /**
     * 锁定库存余额
     */
    private String qty_lock_inventory;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private String u_time;

    /**
     * 变更开始时间
     */
    private LocalDateTime start_time;

    /**
     * 变更结束时间
     */
    private LocalDateTime over_time;

    /**
     * 单位
     */
    private String unit;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 主键 id 集合
     */
    private Integer[] ids;


}
