package com.xinyirun.scm.bean.system.vo.business.inventory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDailyAveragePriceVo implements Serializable {

    private static final long serialVersionUID = 6180851053494697071L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 物料规格id
     */
    private Integer sku_id;

    /**
     * 物料规格code
     */
    private String sku_code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区code
     */
    private String location_code;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位code
     */
    private String bin_code;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 时间
     */
    private LocalDateTime dt;

}
