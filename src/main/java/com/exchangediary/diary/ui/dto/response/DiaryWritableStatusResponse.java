package com.exchangediary.diary.ui.dto.response;

import lombok.Builder;

@Builder
public record DiaryWritableStatusResponse(
        Boolean isMyOrder,
        Boolean writtenTodayDiary,
        Long viewableDiaryId
) {
    public static DiaryWritableStatusResponse of(Boolean isMyOrder, Boolean writtenTodayDiary, Long diaryId) {
        return DiaryWritableStatusResponse.builder()
                .isMyOrder(isMyOrder)
                .writtenTodayDiary(writtenTodayDiary)
                .viewableDiaryId(diaryId)
                .build();
    }
}
