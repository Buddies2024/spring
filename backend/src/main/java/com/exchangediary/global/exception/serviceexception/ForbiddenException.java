package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class ForbiddenException extends ServiceException {
    public ForbiddenException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
