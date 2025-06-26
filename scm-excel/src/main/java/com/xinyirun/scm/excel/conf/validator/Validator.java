package com.xinyirun.scm.excel.conf.validator;

import com.xinyirun.scm.common.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * validator 类
 *
 * @author zxh
 * @date 2016/1/5
 */
public abstract class Validator {

    protected String errorMsg;

    protected String defaultMsg;

    /**
     * 抽象方法，验证
     * @param input
     * @return
     */
    public abstract <T> boolean validate(String input, T rowData, ArrayList ... lists);

    public String getErrorMsg() {
        if (StringUtils.isEmpty(errorMsg)) {
            return defaultMsg;
        }
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
