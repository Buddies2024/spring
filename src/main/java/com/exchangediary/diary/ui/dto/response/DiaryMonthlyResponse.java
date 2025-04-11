package com.exchangediary.diary.ui.dto.response;

import com.exchangediary.diary.domain.dto.DiaryInMonthly;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DiaryMonthlyResponse(
        List<DiaryDayResponse> days
) {
    public static DiaryMonthlyResponse of(List<DiaryInMonthly> diaries, LocalDate lastViewableDiaryDate) {
        List<DiaryDayResponse> days = diaries.stream()
                .map(day -> DiaryDayResponse.of(day, lastViewableDiaryDate))
                .toList();

        return DiaryMonthlyResponse.builder()
                .days(days)
                .build();
    }
}
