package com.xinyirun.scm.excel.conf.convertor;

import com.xinyirun.scm.common.utils.string.StringUtils;

/**
 *
 * @author zxh
 * @date 2019/8/10
 */
public abstract class BaseConvertor implements Convertor {
//    @Override
//    public String convert(Object input) {
//        if (isEmptyObj(input)) {
//            return "";
//        }
//        return doConvert(input);
//    }

//    protected abstract String doConvert(Object input);

    private boolean isEmptyObj(Object input) {
        return input == null || "".equals(input.toString());
    }

    @Override
    public Object convertToType(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        return doConvertToType(input);
    }

    protected abstract Object doConvertToType(String input);
}
