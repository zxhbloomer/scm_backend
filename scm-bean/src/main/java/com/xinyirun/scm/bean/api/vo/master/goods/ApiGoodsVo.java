package com.xinyirun.scm.bean.api.vo.master.goods;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 商品API
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "商品API", description = "商品API")
public class ApiGoodsVo implements Serializable {

    private static final long serialVersionUID = 387556203476999593L;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品编码
     */
    private String code;

    /**
     * 类别编码
     */
    private String category_code;

    public String getCodeAppCode() {
        return code;
    }

    public String getNameCategoryCode() {
        return name+category_code;
    }

}
