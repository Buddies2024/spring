package com.exchangediary.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_DATE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜입니다."),
    DIARY_DUPLICATED(HttpStatus.BAD_REQUEST, "오늘 일기는 작성 완료되었습니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 이름입니다."),
    PROFILE_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 선택된 캐릭터입니다."),

    NEED_TO_REQUEST_TOKEN(HttpStatus.UNAUTHORIZED, "쿠키에서 토큰을 찾을 수 없습니다."),
    INVALID_AUTHORIZATION_TYPE(HttpStatus.UNAUTHORIZED, "인증 타입이 유효하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    NOT_EXIST_MEMBER_TOKEN(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자의 토큰입니다."),
    JWT_TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "jwt 토큰 인증에 실패했습니다."),

    GROUP_FORBIDDEN(HttpStatus.FORBIDDEN, "그룹 접근 권한이 없습니다."),
    DIARY_WRITE_FORBIDDEN(HttpStatus.FORBIDDEN, "일기 작성 권한이 없습니다."),
    DIARY_VIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "일기 조회 권한이 없습니다."),
    GROUP_LEADER_FORBIDDEN(HttpStatus.FORBIDDEN, "방장 권한이 없습니다."),
    GROUP_LEADER_LEAVE_FORBIDDEN(HttpStatus.FORBIDDEN, "그룹 리더는 방을 나갈 수 없습니다."),
    COMMENT_WRITE_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글 작성 권한이 없습니다."),

    NOT_FOUND(HttpStatus.NOT_FOUND, "잘못된 경로입니다."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "일기를 찾을 수 없습니다."),
    UPLOAD_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "일기 업로드 이미지를 찾을 수 없습니다."),
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."),
    GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹원을 찾을 수 없습니다."),
    GROUP_LEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹장을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다."),
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "member id와 매핑된 fcm 토큰을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    FULL_MEMBERS_OF_GROUP(HttpStatus.CONFLICT, "그룹원이 꽉 차\n해당 그룹에 들어갈 수 없습니다."),
    ALREADY_SKIP_ORDER_TODAY(HttpStatus.CONFLICT, "이미 한 번 건너뛰었어요!\n내일 다시 건너뛸 수 있어요."),

    INVALID_IMAGE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 형식입니다."),

    FAILED_TO_LOGIN_KAKAO(HttpStatus.INTERNAL_SERVER_ERROR, "kakao 로그인에 실패했습니다."),
    FAILED_TO_ISSUE_KAKAO_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "kakao 토큰 발급에 실패했습니다."),
    FAILED_TO_GET_KAKAO_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "kakao 사용자 정보 조회에 실패했습니다."),
    FAILED_TO_SEND_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다."),
    FAILED_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "사진 업로드에 실패했습니다.");

    private final HttpStatus statusCode;
    private final String message;
}