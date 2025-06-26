package com.xinyirun.scm.excel.bean.importconfig.template.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

/**
 * @author zxh
 * @date 2019-08-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameAndValue implements Serializable {
    private static final long serialVersionUID = 467871600970028042L;

    private String name;
    private Object value;
}
