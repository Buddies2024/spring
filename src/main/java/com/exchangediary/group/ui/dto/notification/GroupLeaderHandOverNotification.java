package com.exchangediary.group.ui.dto.notification;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

@Builder
public record GroupLeaderHandOverNotification(
        Long oldLeaderId,
        Long newLeaderId,
        String newLeaderNickname
) {
    public static GroupLeaderHandOverNotification of(GroupMember oldLeader, GroupMember newLeader) {
        return GroupLeaderHandOverNotification.builder()
                .oldLeaderId(oldLeader.getMember().getId())
                .newLeaderId(newLeader.getMember().getId())
                .newLeaderNickname(newLeader.getNickname())
                .build();
    }
}
