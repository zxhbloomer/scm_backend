package com.xinyirun.scm.excel.conf.validator;

import com.xinyirun.scm.common.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordian on 2016/1/5.
 */
public class RequiredValidator extends Validator {

    public RequiredValidator() {
        defaultMsg = "不能为空";
    }

    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        return StringUtils.isNotEmpty(input);
    }
}
