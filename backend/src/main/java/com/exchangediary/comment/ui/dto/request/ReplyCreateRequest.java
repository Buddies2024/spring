package com.exchangediary.comment.ui.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ReplyCreateRequest(
        @NotEmpty(message = "답글 내용을 입력해주세요.") String content
) {
}
