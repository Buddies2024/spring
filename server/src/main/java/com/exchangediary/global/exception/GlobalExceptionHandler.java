package com.exchangediary.global.exception;

import com.exchangediary.global.exception.serviceexception.duplicate.DuplicateException;
import com.exchangediary.global.exception.serviceexception.invliadrange.DateRangeException;
import com.exchangediary.global.exception.serviceexception.invliadrange.InvalidRangeException;
import com.exchangediary.global.exception.serviceexception.notfound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidArgumentException(MethodArgumentNotValidException exception) {
        try {
            return ApiErrorResponse.from(Objects.requireNonNull(exception.getFieldError()));
        } catch (NullPointerException e) {
            return ApiErrorResponse.from(HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMissingParameterException(MissingServletRequestParameterException exception) {
        return ApiErrorResponse.from(exception);
    }

    @ExceptionHandler(InvalidRangeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidRangeException(DateRangeException exception) {
        return ApiErrorResponse.from(exception.getErrorCode(), exception.getMessage(), exception.getValue());
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleDuplicateException(DuplicateException exception) {
        return ApiErrorResponse.from(exception.getErrorCode(), exception.getMessage(), exception.getValue());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(NotFoundException exception) {
        return ApiErrorResponse.from(exception.getErrorCode(), exception.getMessage(), exception.getValue());
    }
}
