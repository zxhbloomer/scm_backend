package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 入库计划详情返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库计划详情返回结果", description = "入库计划详情返回结果")
public class ApiInPlanDetailResultVo implements Serializable {

    private static final long serialVersionUID = 7057014511787576464L;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 序号
     */
    private String code;

    /**
     * 物料code
     */
    private String goods_code;

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
     * 入库库存数量
     */
    private BigDecimal count;

    /**
     * 库存计量单位
     */
    private String unit;
}
