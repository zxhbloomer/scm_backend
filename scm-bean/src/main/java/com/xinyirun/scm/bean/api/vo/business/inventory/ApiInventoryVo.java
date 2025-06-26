package com.xinyirun.scm.bean.api.vo.business.inventory;


// import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 库存
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存", description = "库存")
public class ApiInventoryVo implements Serializable {

    private static final long serialVersionUID = -5950858523604652092L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String spec;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 货主名
     */
    private String owner_name;

    /**
     * 库存数量
     */
    private BigDecimal qty_avaible;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 是否启用分页
     */
    private Boolean paging;

}
