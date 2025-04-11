package com.exchangediary.diary.ui.dto.notification;

import com.exchangediary.diary.domain.entity.Diary;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DiaryWriteNotification(
        Long diaryId,
        String writerNickname,
        LocalDate createdAt
) {
    public static DiaryWriteNotification from(Diary diary) {
        return DiaryWriteNotification.builder()
                .diaryId(diary.getId())
                .writerNickname(diary.getGroupMember().getNickname())
                .createdAt(diary.getCreatedAt().toLocalDate())
                .build();
    }
}
