package com.exchangediary.group.ui.dto.response;

import lombok.Builder;

@Builder
public record GroupIdResponse(
    String groupId
) {
    public static GroupIdResponse from(String groupId) {
        return GroupIdResponse.builder()
                .groupId(groupId)
                .build();
    }
}
