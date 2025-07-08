package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class InvalidDateException extends ServiceException {
    public InvalidDateException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
