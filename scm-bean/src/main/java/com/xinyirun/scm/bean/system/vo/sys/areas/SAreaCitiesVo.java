package com.xinyirun.scm.bean.system.vo.sys.areas;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 市
 * </p>
 *
 * @author zxh
 * @since 2019-10-31
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "市", description = "市")
@EqualsAndHashCode(callSuper=false)
public class SAreaCitiesVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1579651769183614222L;

    private Long id;

    /**
     * 市级编号
     */
    private Integer code;

    /**
     * 市名称
     */
    private String name;

    /**
     * 省级编号
     */
    private Integer province_code;

}
