package com.exchangediary.diary.ui.dto.notification;

import com.exchangediary.diary.domain.entity.Diary;
import lombok.Builder;

@Builder
public record DiaryWriteNotification(
        Long diaryId,
        String writerNickname
) {
    public static DiaryWriteNotification from(Diary diary) {
        return DiaryWriteNotification.builder()
                .diaryId(diary.getId())
                .writerNickname(diary.getGroupMember().getNickname())
                .build();
    }
}
