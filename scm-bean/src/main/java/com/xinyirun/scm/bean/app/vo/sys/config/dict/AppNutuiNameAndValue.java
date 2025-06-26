package com.xinyirun.scm.bean.app.vo.sys.config.dict;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取能在Nutui显示数据的字典数据
 * @author zxh
 * @date 2019-08-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppNutuiNameAndValue implements Serializable {


    @Serial
    private static final long serialVersionUID = -325256523086769367L;

    /**
     * 查询条件字典code
     */
    private String code;
    /**
     * 获取能在Nutui显示数据的字典数据：定义显示的label
     */
    private String label;

    /**
     * 获取能在Nutui显示数据的字典数据定义value
     */
    private String value;
}
