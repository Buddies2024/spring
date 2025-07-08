package com.exchangediary.diary.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DiaryRequest (
        @NotEmpty(message = "내용을 입력해주세요.") List<DiaryContentRequest> contents,
        @NotBlank(message = "오늘의 기분을 골라주세요.") String todayMood
) {
}
