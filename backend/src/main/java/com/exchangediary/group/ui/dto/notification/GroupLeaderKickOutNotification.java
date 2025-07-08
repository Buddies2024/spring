package com.exchangediary.group.ui.dto.notification;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

@Builder
public record GroupLeaderKickOutNotification(
        Long kickOutMemberId,
        String kickOutMemberNickname
) {
    public static GroupLeaderKickOutNotification from(GroupMember kickOutMember) {
        return GroupLeaderKickOutNotification.builder()
                .kickOutMemberId(kickOutMember.getMember().getId())
                .kickOutMemberNickname(kickOutMember.getNickname())
                .build();
    }
}
