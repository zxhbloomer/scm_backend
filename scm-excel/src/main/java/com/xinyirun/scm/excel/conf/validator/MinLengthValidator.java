package com.xinyirun.scm.excel.conf.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordian on 2016/1/5.
 */
public class MinLengthValidator extends Validator {

    private Integer minLength;


    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
        defaultMsg = "最小长度为" + minLength;
    }

    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        if (minLength == null) {
            throw new IllegalArgumentException("min未设置");
        }
        return input.length() >= minLength;
    }

}
