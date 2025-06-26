package com.xinyirun.scm.excel.conf.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordian on 2016/1/5.
 */
public class NumValidator extends Validator {

    public NumValidator() {
        defaultMsg = "不是有效的数字";
    }

    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        try {
            new Float(input);
            return true;
        } catch (NumberFormatException e) {
            defaultMsg = "不是有效的数字";
            return false;
        }
    }
}
