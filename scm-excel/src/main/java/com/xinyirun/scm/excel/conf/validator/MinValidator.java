package com.xinyirun.scm.excel.conf.validator;

import com.xinyirun.scm.common.utils.string.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by gordian on 2016/1/5.
 */
public class MinValidator extends Validator {

    private BigDecimal min;

    public void setMin(BigDecimal min) {
        this.min = min;
        defaultMsg = "需要填写值大于" + min;
    }

    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        if (min == null) {
            throw new IllegalArgumentException("min未设置");
        }
        try {
            if (StringUtils.isNotEmpty(input)) {
                if ((new BigDecimal(input)).compareTo(min) > 0) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            } else {
                return Boolean.TRUE;
            }

        } catch (NumberFormatException e) {
            defaultMsg = "不是有效的数值";
            return false;
        }
    }

}
