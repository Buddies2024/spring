package com.exchangediary.diary.ui.dto.request;

import jakarta.validation.constraints.NotNull;

public record DiaryContentRequest(
        @NotNull String content
) {
}
