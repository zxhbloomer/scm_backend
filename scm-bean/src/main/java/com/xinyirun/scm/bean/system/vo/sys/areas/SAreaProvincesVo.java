package com.xinyirun.scm.bean.system.vo.sys.areas;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 省份
 * </p>
 *
 * @author zxh
 * @since 2019-10-31
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "省份", description = "省份")
@EqualsAndHashCode(callSuper=false)
public class SAreaProvincesVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 8199680070615544291L;

    private Long id;

    /**
     * 省份编号
     */
    private Integer code;

    /**
     * 省份名称
     */
    private String name;


}
