package com.xinyirun.scm.common.exception.fund;

import com.xinyirun.scm.common.enums.FundResultEnum;
import com.xinyirun.scm.common.enums.InventoryResultEnum;

/**
 * 业务异常
 * 
 * @author
 */
public class FundBusinessException extends RuntimeException {

    private static final long serialVersionUID = 4369986987335685075L;

    private String message;
    private FundResultEnum enumData;

    public FundBusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public FundBusinessException(FundResultEnum enumData) {
        this.enumData = enumData;
        this.message = enumData.getMsg();
    }

    public FundBusinessException(FundResultEnum enumDatae, String msg) {
        this.enumData = enumData;
        this.message = msg;
    }

    public FundBusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
    public FundResultEnum getEnumData(){
        return enumData;
    }
}
