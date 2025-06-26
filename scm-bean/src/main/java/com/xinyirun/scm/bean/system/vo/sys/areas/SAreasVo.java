package com.xinyirun.scm.bean.system.vo.sys.areas;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 区
 * </p>
 *
 * @author zxh
 * @since 2019-10-31
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "区", description = "区")
@EqualsAndHashCode(callSuper=false)
public class SAreasVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 4739803322473167883L;

    private Long id;

    /**
     * 区编号
     */
    private Integer code;

    /**
     * 地区名称
     */
    private String name;

    /**
     * 市级编号
     */
    private Integer city_code;

    /**
     * 省级编号
     */
    private Integer province_code;


}
