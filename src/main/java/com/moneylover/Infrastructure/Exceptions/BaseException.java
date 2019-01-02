package com.moneylover.Infrastructure.Exceptions;

public class BaseException extends Exception {
    private int code;

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
