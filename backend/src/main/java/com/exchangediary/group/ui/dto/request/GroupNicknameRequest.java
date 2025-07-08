package com.exchangediary.group.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupNicknameRequest(
        @NotBlank(message = "공백을 포함할 수 없습니다.") String nickname
) {
}
