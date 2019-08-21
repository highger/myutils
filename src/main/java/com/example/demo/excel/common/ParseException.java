package com.example.demo.excel.common;

/**
 * Created by mgy on 2019/8/21
 */
public class ParseException extends Exception {


    private static final long serialVersionUID = -7612718726198915385L;


    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    protected ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }

}
