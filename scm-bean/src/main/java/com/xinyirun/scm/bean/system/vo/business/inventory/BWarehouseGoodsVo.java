package com.xinyirun.scm.bean.system.vo.business.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BWarehouseGoodsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 3813666006864778999L;

    private Integer no;

    /**
     * 仓库类型
     */
    private String warehouse_type;

    /**
     * 仓库类型名称
     */
    private String warehouse_type_name;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库ID
     */
    private Long warehouse_id;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 合计数量
     */
    private BigDecimal qty;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    private Long staff_id;

    /**
     * 入库数量
     */
    private BigDecimal in_qty;

    /**
     * 出库数量
     */
    private BigDecimal out_qty;

    /**
     * 退货数量
     */
    private BigDecimal return_qty;

    /**
     * 退货总数
     */
    private BigDecimal return_sum_qty;

    /**
     * 库存调整数量
     */
    private BigDecimal qty_diff;

    /**
     * 类型， 1是按仓库类型分组， 2是按仓库Id分组
     */
    private String query_type;

    private Integer[] warehouse_ids;
    /**
     * 仓库类型， 字典
     */
    private String type;

    private String goods_code;

    private String type_id;

    private String id;

    private String batch;

}
