package com.xinyirun.scm.bean.api.vo.business.monitor;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 承运订单商品
 * </p>
 *
 * @author wwl
 * @since 2023-04-06
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiCarriageOrderGoodsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -291239322584204433L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 序号
     */
    private Integer no;


    /**
     * 商品编号
     */
    private String goods_code;


    /**
     * 商品规格编号
     */
    private String sku_code;

    /**
     * 单位code
     */
    private String unit_code;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 货值单价(含税)
     */
    private BigDecimal price;

    /**
     * 货值总金额(含税)
     */
    private BigDecimal amount;

    /**
     * 货值不含税金额
     */
    private BigDecimal amount_not;

    /**
     * 税率
     */
    private BigDecimal rate;

    /**
     * 货值税款金额
     */
    private BigDecimal tax_amount;

    /**
     * 备注
     */
    private String remark;



}
