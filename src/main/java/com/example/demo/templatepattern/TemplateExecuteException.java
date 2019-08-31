package com.example.demo.templatepattern;

/**
 * Created by mgy on 2019/8/31
 */
public class TemplateExecuteException extends RuntimeException {
    private static final long serialVersionUID = 1640129253242448688L;

    public TemplateExecuteException() {
        super();
    }

    public TemplateExecuteException(String message) {
        super(message);
    }

    protected TemplateExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public TemplateExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateExecuteException(Throwable cause) {
        super(cause);
    }
}