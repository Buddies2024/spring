package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class DuplicateException extends ServiceException {
    public DuplicateException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
