package com.exchangediary.global.exception;

import com.exchangediary.global.exception.serviceexception.ServiceException;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ApiErrorResponse(
        int statusCode,
        String message,
        String value
) {
    public static ApiErrorResponse from(HttpStatus status) {
        return ApiErrorResponse.builder()
                .statusCode(status.value())
                .build();
    }

    public static ApiErrorResponse from(ServiceException exception) {
        return ApiErrorResponse.builder()
                .statusCode(exception.getErrorCode().getStatusCode().value())
                .message(exception.getMessage())
                .value(exception.getValue())
                .build();
    }
}
