package com.xinyirun.scm.bean.api.vo.business.largescreen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购销售合同量
 *
 * @Author: wangqianfeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiContractQtyStatisticsVo implements Serializable {

    private static final long serialVersionUID = -8366042547347798234L;
    /**
     * 水稻采购量
     */
    private BigDecimal qty_rice = BigDecimal.ZERO;

    /**
     * 掺混物采购量
     */
    private BigDecimal qty_blends = BigDecimal.ZERO;

    /**
     * 产成品
     */
    private BigDecimal qty_product = BigDecimal.ZERO;

    /**
     * 副产品
     */
    private BigDecimal qty_coproduct = BigDecimal.ZERO;
}
