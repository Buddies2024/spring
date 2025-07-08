package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
