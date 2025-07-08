package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class MessagingFailureException extends ServiceException {
    public MessagingFailureException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
