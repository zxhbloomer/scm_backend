package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 返回入库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回入库计划", description = "返回入库计划")
public class ApiInPlanBo implements Serializable {

    private static final long serialVersionUID = -164215255427183944L;
    /**
     * url
     */
    private String url;

    /**
     * 返回bo
     */
    private ApiInPlanResultBo bo;
}
