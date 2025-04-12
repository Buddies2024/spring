package com.exchangediary.group.ui.dto.notification;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

@Builder
public record GroupJoinNotification(
        String joinMemberNickname
) {
    public static GroupJoinNotification from(GroupMember joinMember) {
        return GroupJoinNotification.builder()
                .joinMemberNickname(joinMember.getNickname())
                .build();
    }
}
