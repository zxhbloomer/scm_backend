package com.xinyirun.scm.bean.system.vo.business.price;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
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
 * @since 2022-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialPriceVo implements Serializable {

    private static final long serialVersionUID = 309510450779246124L;

    /**
     * 主键 id
     */
    private Integer id;

    /**
     * 物料 ID
     */
    private Integer goods_id;

    private String goods_code;

    private String goods_name;

    private Integer sku_id;

    /**
     * 规格编号 业务中台和wms唯一关联code
     */
    private String sku_code;

    private String sku_name;

    /**
     * 0 物料转换计算转换后15天加权单价
     * 	从转换前找到price
     * 		关联条件
     * 			dt
     * 			source_sku_id
     * 1 未物料转换，计算自己的转换后15天加权单价
     * 	从转换前找到price
     * 		dt
     * 		source_sku_id
     * 2 大宗商品实时单价
     * 	从大宗商品单价
     */
    private String type;

    /**
     * b_material_convert_price.code
     * 	type=0,1
     * b_goods_price.code
     * 	type=2
     */
    private String query_code;

    private BigDecimal price;

    private LocalDateTime c_time;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

}
