package com.exchangediary.group.ui.dto.notification;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

@Builder
public record GroupLeaderSkipDiaryNotification(
        Long skipDiaryMemberId
) {
    public static GroupLeaderSkipDiaryNotification from(GroupMember skipMember) {
        return GroupLeaderSkipDiaryNotification.builder()
                .skipDiaryMemberId(skipMember.getMember().getId())
                .build();
    }
}
