package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class NotFoundException extends ServiceException {
     public NotFoundException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
