package com.exchangediary.global.exception.serviceexception;

import com.exchangediary.global.exception.ErrorCode;

public class KakaoLoginFailureException extends ServiceException {
    public KakaoLoginFailureException(ErrorCode errorCode, String message, String value) {
        super(errorCode, message, value);
    }
}
