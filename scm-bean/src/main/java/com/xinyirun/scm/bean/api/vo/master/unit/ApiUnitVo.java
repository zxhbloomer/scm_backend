package com.xinyirun.scm.bean.api.vo.master.unit;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 单位
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "单位数据", description = "单位数据")
public class ApiUnitVo implements Serializable {

    private static final long serialVersionUID = -4835276668618935015L;
    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;

    public String getCodeAppCode() {
        return code;
    }

}
