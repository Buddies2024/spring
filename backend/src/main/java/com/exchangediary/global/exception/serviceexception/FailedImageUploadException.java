package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class FailedImageUploadException extends ServiceException {
    public FailedImageUploadException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
