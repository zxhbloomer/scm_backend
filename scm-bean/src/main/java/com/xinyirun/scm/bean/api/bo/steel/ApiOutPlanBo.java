package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 返回出库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回出库计划", description = "返回出库计划")
public class ApiOutPlanBo implements Serializable {

    private static final long serialVersionUID = -5228676754963615268L;


    /**
     * url
     */
    private String url;

    /**
     * 返回bo
     */
    private ApiOutPlanResultBo bo;
}
