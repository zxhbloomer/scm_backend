package com.xinyirun.scm.bean.api.vo.business.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class ApiBWkCoDetailVo implements Serializable {

    private static final long serialVersionUID = -6503295649295077246L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 商品名称
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
    private Integer num;

    /**
     * 金额(含税)
     */
    private BigDecimal amount;

    /**
     * 税率
     */
    private BigDecimal rate;

    /**
     * 货值不含税金额
     */
    private BigDecimal amount_not;

    /**
     * 货值税款金额
     */
    private BigDecimal tax_amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 错误类型 1:规格code为空 2:未同步该规格 3:单位未同步
     */
    private String flag;
}
