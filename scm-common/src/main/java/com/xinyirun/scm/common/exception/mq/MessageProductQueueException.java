package com.xinyirun.scm.common.exception.mq;

/**
 * message queue 异常
 * 
 * @author
 */
public class MessageProductQueueException extends RuntimeException {

    private static final long serialVersionUID = -27869893850259222L;

    private String message;

    public MessageProductQueueException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public MessageProductQueueException(String message) {
        this.message = message;
    }

    public MessageProductQueueException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
