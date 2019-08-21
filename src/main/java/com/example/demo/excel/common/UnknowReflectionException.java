package com.example.demo.excel.common;

/**
 * 反射时将 checked exception 转化为 RuntimeException.
 * 表示反射产生异常的情况属于非预期的情况
 *
 *  Created by mgy on 2019/8/21
 */
public class UnknowReflectionException extends RuntimeException {

    private static final long serialVersionUID = -3676736680925501124L;

    public UnknowReflectionException() {
        super();
    }


    public UnknowReflectionException(Throwable cause) {
        super(cause);
    }

    protected UnknowReflectionException(String message,
                                            Throwable cause,
                                            boolean enableSuppression,
                                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnknowReflectionException(String message) {
        super(message);
    }

    public UnknowReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
