package com.xinyirun.scm.bean.api.vo.master.goods;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 类别API
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "类别API", description = "类别API")
public class ApiCategoryVo implements Serializable {

    private static final long serialVersionUID = -6549608805354431443L;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 类别编码
     */
    private String code;

    /**
     * 行业编码
     */
    private String industry_code;

    public String getCodeAppCode() {
        return code;
    }

    public String getNameIndustryCode() {
        return name+industry_code;
    }
}
