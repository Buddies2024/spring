package com.exchangediary.diary.ui.dto.response;

import com.exchangediary.diary.domain.entity.DiaryContent;
import lombok.Builder;

import java.util.List;

@Builder
public record DiaryContentResponse(
        String content
) {
    public static List<DiaryContentResponse> fromContents(List<DiaryContent> contents) {
        return contents.stream()
                .map(DiaryContentResponse::from)
                .toList();
    }

    private static DiaryContentResponse from(DiaryContent diaryContent) {
        return DiaryContentResponse.builder()
                .content(diaryContent.getContent())
                .build();
    }
}
