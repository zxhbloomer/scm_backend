package com.xinyirun.scm.common.exception.mq;

/**
 * message queue 异常
 * 
 * @author
 */
public class MessageConsumerQueueException extends RuntimeException {

    private static final long serialVersionUID = -1054735990234432481L;

    private String message;

    public MessageConsumerQueueException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public MessageConsumerQueueException(String message) {
        this.message = message;
    }

    public MessageConsumerQueueException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
