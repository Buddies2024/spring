package com.exchangediary.group.ui.dto.response;

import com.exchangediary.group.domain.entity.Group;
import lombok.Builder;

@Builder
public record GroupCreateResponse(
        String groupId,
        String code
) {
    public static GroupCreateResponse of(Group group) {
        return GroupCreateResponse.builder()
                .groupId(group.getId())
                .build();
    }
}
