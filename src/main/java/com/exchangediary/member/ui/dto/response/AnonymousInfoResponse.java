package com.exchangediary.member.ui.dto.response;

import lombok.Builder;

@Builder
public record AnonymousInfoResponse(
        Boolean shouldLogin,
        String groupId
) {
    public static AnonymousInfoResponse of(boolean shouldLogin, String groupId) {
        return AnonymousInfoResponse.builder()
                .shouldLogin(shouldLogin)
                .groupId(groupId)
                .build();
    }
}
