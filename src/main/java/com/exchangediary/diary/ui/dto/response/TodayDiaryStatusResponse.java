package com.exchangediary.diary.ui.dto.response;

import lombok.Builder;

@Builder
public record TodayDiaryStatusResponse(
        Boolean isMyOrder,
        Long todayDiaryId,
        Boolean canViewTodayDiary
) {
    public static TodayDiaryStatusResponse of(boolean isMyOrder, Long diaryId, boolean canViewTodayDiary) {
        return TodayDiaryStatusResponse.builder()
                .isMyOrder(isMyOrder)
                .todayDiaryId(diaryId)
                .canViewTodayDiary(canViewTodayDiary)
                .build();
    }
}
