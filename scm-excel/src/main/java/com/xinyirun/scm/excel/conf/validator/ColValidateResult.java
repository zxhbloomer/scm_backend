package com.xinyirun.scm.excel.conf.validator;

import com.xinyirun.scm.excel.bean.importconfig.template.data.DataCol;
import lombok.Data;

/**
 *
 * @author zxh
 * @date 2016/1/12
 */
@Data
public class ColValidateResult {


  private DataCol dataCol;

  private String errorMsg;
  private boolean isSuccess = true;

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
    isSuccess = false;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public boolean isFailed() {
    return !isSuccess;
  }

}
