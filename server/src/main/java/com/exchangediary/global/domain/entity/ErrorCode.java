package com.exchangediary.global.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(400, "입력이 유효하지 않습니다."),

    DIARY_NOT_FOUND(404, "일기를 찾을 수 없습니다."),
    STICKER_IMAGE_NOT_FOUND(404, "스티커 이미지를 찾을 수 없습니다."),
    MOOD_IMAGE_NOT_FOUND(404, "기분 이미지를 찾을 수 없습니다."),
    IMAGE_UPLOAD_ERROR(500, "이미지 업로드 중 오류가 발생했습니다.");
    private final int statusCode;
    private final String message;
}
