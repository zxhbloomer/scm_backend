package com.xinyirun.scm.common.exception.inventory;

import com.xinyirun.scm.common.enums.InventoryResultEnum;

/**
 * 业务异常
 * 
 * @author
 */
public class InventoryBusinessException extends RuntimeException {

    private static final long serialVersionUID = 4369986987335685075L;

    private String message;
    private InventoryResultEnum enumData;

    public InventoryBusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public InventoryBusinessException(InventoryResultEnum enumData) {
        this.enumData = enumData;
        this.message = enumData.getMsg();
    }

    public InventoryBusinessException(InventoryResultEnum enumDatae, String msg) {
        this.enumData = enumData;
        this.message = msg;
    }

    public InventoryBusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
    public InventoryResultEnum getEnumData(){
        return enumData;
    }
}
