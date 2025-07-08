package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class ConfilctException extends ServiceException {
    public ConfilctException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
