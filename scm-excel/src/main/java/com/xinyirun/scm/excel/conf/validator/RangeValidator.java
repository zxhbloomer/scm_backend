package com.xinyirun.scm.excel.conf.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordian on 2016/1/5.
 */
public class RangeValidator extends Validator {

    private Float min;
    private Float max;

    public void setMin(Float min) {
        this.min = min;
    }

    public void setMax(Float max) {
        this.max = max;
    }

    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("参数设置不正确");
        }
        defaultMsg = String.format("值必须在[%s-%s]间", min, max);
        try {
            Float inputVal = new Float(input);
            return inputVal >= min && inputVal <= max;
        } catch (NumberFormatException e) {
            defaultMsg = "不是有效的数值";
            return false;
        }

    }

}
