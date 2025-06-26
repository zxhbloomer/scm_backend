package com.xinyirun.scm.excel.bean.importconfig.template.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxh
 * @date 2019-08-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorBean implements Serializable {
    private static final long serialVersionUID = 5715390875407475426L;

    /**
     * Validator名称
     */
    private String validtorName;

    /**
     * 参数
     */
    private List<NameAndValue> param;
}
