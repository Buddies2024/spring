package com.exchangediary.group.ui.dto.response;

import lombok.Builder;

@Builder
public record GroupNicknameVerifyResponse(
        boolean verification
) {
    public static GroupNicknameVerifyResponse from(boolean verification) {
        return GroupNicknameVerifyResponse.builder()
                .verification(verification)
                .build();
    }
}
