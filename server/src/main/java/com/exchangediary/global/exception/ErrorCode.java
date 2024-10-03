package com.exchangediary.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_DATE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜입니다."),
    DIARY_DUPLICATED(HttpStatus.BAD_REQUEST, "오늘 일기는 작성 완료되었습니다."),

    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "일기를 찾을 수 없습니다."),
    UPLOAD_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "일기 업로드 이미지를 찾을 수 없습니다."),
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다."),

    FULL_MEMBERS_OF_GROUP(HttpStatus.CONFLICT, "그룹내의 인원수가 초과되었습니다."),


    FAILED_TO_LOGIN_KAKAO(HttpStatus.INTERNAL_SERVER_ERROR, "kakao 로그인에 실패했습니다."),
    FAILED_TO_ISSUE_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "kakao 토큰 발급에 실패했습니다."),
    FAILED_TO_GET_KAKAO_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "kakao 사용자 정보 조회에 실패했습니다."),
    FAILED_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 중 오류가 발생했습니다.");

    private final HttpStatus statusCode;
    private final String message;
}