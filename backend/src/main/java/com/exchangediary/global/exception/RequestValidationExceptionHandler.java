package com.exchangediary.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class RequestValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidArgumentException(MethodArgumentNotValidException exception) {
        try {
            log.error("{}", String.format("%s", exception.getFieldError().getDefaultMessage()));
            return ApiErrorResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(String.format("%s", exception.getFieldError().getDefaultMessage()))
                    .value((String) exception.getFieldError().getRejectedValue())
                    .build();
        } catch (NullPointerException e) {
            return ApiErrorResponse.from(HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMissingParameterException(MissingServletRequestParameterException exception) {
        log.error("{}", String.format("%s", exception.getMessage()));
        return ApiErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidArgumentTypeException(MethodArgumentTypeMismatchException exception) {
        log.error("{}", String.format("%s", exception.getMessage()));
        return ApiErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .value((String) exception.getValue())
                .build();
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidNumberFormatException(NumberFormatException exception) {
        log.error("{}", String.format("%s", exception.getMessage()));
        return ApiErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .value(exception.getLocalizedMessage())
                .build();
    }
}
