package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库计划详情返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划详情返回结果", description = "出库计划详情返回结果")
public class ApiOutPlanDetailResultVo implements Serializable {

    private static final long serialVersionUID = -4131111336365850227L;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 序号
     */
    private String code;

    /**
     * 库存id
     */
    private Integer inventory_id;

    /**
     * 物料名
     */
    private String goods_name;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String spec;

    /**
     * 品名
     */
    private String pm;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名
     */
    private String warehouse_name;

    /**
     * 出库库存数量
     */
    private BigDecimal count;

    /**
     * 库存计量单位
     */
    private String unit;
}
