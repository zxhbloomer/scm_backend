package com.xinyirun.scm.excel.conf.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordian on 2016/1/5.
 */
public class RegexValidator extends Validator {

    private String regex;

    public RegexValidator() {
        defaultMsg = "格式不匹配";
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        return input.matches(regex);
    }

}
