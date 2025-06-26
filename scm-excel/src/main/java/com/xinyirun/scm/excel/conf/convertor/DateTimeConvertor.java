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
public class DateTimeConvertor extends BaseConvertor {

    public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

//    @Override
//    public String doConvert(Object input) {
//        return DateFormatUtils.format((Date) input, DATETIME);
//    }

    @Override
    public Object doConvertToType(String input) {
        try {
            return LocalDateTimeUtils.parse(input);
//            return DateTimeUtil.parseDate(input, new String[]{DATETIME});
        } catch (Exception e) {
            throw new SystemExcelException(e);
        }
    }
}
