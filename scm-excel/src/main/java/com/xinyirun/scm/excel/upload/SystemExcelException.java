package com.xinyirun.scm.excel.upload;

/**
 * 异常类
 * @author zxh
 */
public class SystemExcelException extends RuntimeException {

	private static final long serialVersionUID = 1830974553436749465L;

	public SystemExcelException() {

	}

	public SystemExcelException(String message) {
		super(message);
	}

	public SystemExcelException(Throwable cause) {
		super(cause);
	}

	public SystemExcelException(String message, Throwable cause) {
		super(message, cause);
	}

	public SystemExcelException(String message, Throwable cause,
								boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
