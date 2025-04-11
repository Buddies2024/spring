package com.exchangediary.group.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupCodeRequest(
        @NotBlank(message = "그룹코드를 입력해주세요.") String code
) {
}
