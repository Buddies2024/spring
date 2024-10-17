package com.exchangediary.diary.ui.dto.response;

import lombok.Builder;

@Builder
public record DiaryWritableStatusResponse(
        Boolean isMyOrder,
        Boolean writtenTodayDiary
) {
    public static DiaryWritableStatusResponse of(Boolean isMyOrder, Boolean writtenTodayDiary) {
        return DiaryWritableStatusResponse.builder()
                .isMyOrder(isMyOrder)
                .writtenTodayDiary(writtenTodayDiary)
                .build();
    }
}
