package com.xinyirun.scm.bean.api.vo.sync;

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
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBMaterialPriceVo implements Serializable {

    private static final long serialVersionUID = 7421144041693030795L;

    private Integer id;

    private Integer goods_id;

    private String goods_code;

    private String goods_name;

    private Integer sku_id;

    /**
     * 规格编号 业务中台和wms唯一关联code
     */
    private String sku_code;

    private String sku_name;

    private String type;

    private String query_code;

    private BigDecimal price;

    private String c_time;

}
