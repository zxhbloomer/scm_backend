package com.xinyirun.scm.bean.api.vo.master.goods;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 行业API
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "行业API", description = "行业API")
public class ApiIndustryVo implements Serializable {

    private static final long serialVersionUID = 4058295239879152547L;

    /**
     * 行业名称
     */
    private String name;

    /**
     * 行业编码
     */
    private String code;

    /**
     * 板块编码
     */
    private String business_type_code;

    public String getCodeAppCode() {
        return code;
    }

    public String getNameBusinessTypeCode() {
        return name+business_type_code;
    }
}
