package com.exchangediary.group.ui.dto.notification;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

@Builder
public record GroupLeaveNotification(
        String leaveMemberNickname
) {
    public static GroupLeaveNotification from(GroupMember leaveMember) {
        return GroupLeaveNotification.builder()
                .leaveMemberNickname(leaveMember.getNickname())
                .build();
    }
}
