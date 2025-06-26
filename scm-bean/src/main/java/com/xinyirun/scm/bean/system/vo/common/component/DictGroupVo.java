package com.xinyirun.scm.bean.system.vo.common.component;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxh
 * @date 2019/9/24
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "下拉选项的数据", description = "下拉选项的数据")
@EqualsAndHashCode(callSuper=false)
public class DictGroupVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6232704158614451627L;
    /**
     * 组标签
     */
    private String label_code;

    /**
     * 组标签
     */
    private String label;

    private List<NameAndValueVo> options;

}
