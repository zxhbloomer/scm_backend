package com.xinyirun.scm.excel.conf.convertor;

import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.excel.upload.SystemExcelException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author zxh
 * @date 2019/8/10
 */
public class DateConvertor extends BaseConvertor {

//    @Override
//    public String doConvert(Object input) {
//        return DateFormatUtils.format((Date) input, DATE);
//    }

    @Override
    public Object doConvertToType(String input) {
        try {
            return LocalDateTimeUtils.parse(input);
        } catch (Exception e) {
            throw new SystemExcelException(e);
        }
    }
}
