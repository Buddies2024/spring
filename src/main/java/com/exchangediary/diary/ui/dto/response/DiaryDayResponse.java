package com.exchangediary.diary.ui.dto.response;

import com.exchangediary.diary.domain.dto.DiaryInMonthly;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DiaryDayResponse(
        Long id,
        Integer day,
        String profileImage,
        Boolean canView
) {
    public static DiaryDayResponse of(DiaryInMonthly diary, LocalDate lastViewableDiaryDate) {
        return DiaryDayResponse.builder()
                .id(diary.id())
                .day(diary.createdAt().getDayOfMonth())
                .profileImage(diary.profileImage())
                .canView(!diary.createdAt().toLocalDate().isAfter(lastViewableDiaryDate))
                .build();
    }
}
