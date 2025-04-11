package com.exchangediary.diary.domain.dto;

import java.time.LocalDateTime;

public record DiaryInMonthly(
        Long id,
        LocalDateTime createdAt,
        String profileImage
) {
}
