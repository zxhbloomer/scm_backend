package com.xinyirun.scm.bean.system.vo.master.goods.unit;


// import io.swagger.annotations.ApiModel;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 物料规格
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "物料规格单位换算列表", description = "物料规格单位换算列表")
public class MUnitConvertUpdateVo implements Serializable {

    private static final long serialVersionUID = 7253251616291564474L;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 单位换算
     */
    private MGoodsUnitConvertVo unit_calulator;
}
