package com.xinyirun.scm.excel.conf.validator;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zxh
 * @date 2019/1/12
 */
@Data
public class DateTimeValidator extends Validator {
  //
  /**
   * 数组json格式为：["yyyy/MM/dd","yyyy-MM-dd","yyyyMMdd"]
   */
  private String dateFormat_json ;

  public DateTimeValidator() {
    defaultMsg = "无效的日期格式";
  }

  @Override
  public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
    try {
      /**
       * 增加逻辑：
       * 当input为空时，返回true
       */
        if (input == null || input.trim().isEmpty()) {
          return true;
        }
      String [] dateFormat = JSON.parseObject(dateFormat_json, String [] .class);
      LocalDateTime rtn = LocalDateTimeUtils.parse(input, dateFormat);
      if (rtn == null) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
