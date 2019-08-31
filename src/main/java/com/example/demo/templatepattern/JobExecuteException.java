package com.example.demo.templatepattern;

/**
 * Created by mgy on 2019/8/31
 */
public class JobExecuteException extends RuntimeException {
    private static final long serialVersionUID = 1640129253242448688L;

    public JobExecuteException() {
        super();
    }

    public JobExecuteException(String message) {
        super(message);
    }

    protected JobExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public JobExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecuteException(Throwable cause) {
        super(cause);
    }
}