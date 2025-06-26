package com.xinyirun.scm.bean.api.vo.business.in;

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
 * @since 2022-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBInOrderGoodsVo implements Serializable {

    private static final long serialVersionUID = -879620139811405410L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编号
     */
    private String no;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 规格code
     */
    private String sku_name;

    /**
     * 规格code
     */
    private String pm;

    /**
     * 规格code
     */
    private String spec;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 单位code
     */
    private String unit_code;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 单价(含税)
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 金额(含税)
     */
    private BigDecimal amount;

    /**
     * 税率
     */
    private BigDecimal rate;

    /**
     * 交货日期
     */
    private LocalDateTime delivery_date;

    /**
     * 交货方式(1-自提;2-物流)
     */
    private String delivery_type;

}
