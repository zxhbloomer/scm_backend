package com.xinyirun.scm.bean.api.vo.master.goods;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>
 * 板块API
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "板块API", description = "板块API")
public class ApiBusinessTypeVo implements Serializable {

    private static final long serialVersionUID = 3219442543421340875L;

    /**
     * 板块编码
     */
    private String code;

    /**
     * 板块名称
     */
    private String name;

    public String getCodeAppCode() {
        return code;
    }

    public String getNameCodeAppCode() {
        return name;
    }
}
