package com.exchangediary.comment.ui.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull Double xCoordinate,
        @NotNull Double yCoordinate,
        @NotNull Integer page,
        @NotEmpty(message = "댓글 내용을 입력해주세요.") String content
) {
}
