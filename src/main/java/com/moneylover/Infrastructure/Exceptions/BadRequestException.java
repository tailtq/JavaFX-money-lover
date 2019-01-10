package com.moneylover.Infrastructure.Exceptions;

public class BadRequestException extends BaseException {
    public BadRequestException() {
        super("BAD_REQUEST", 400);
    }
}
