package com.exchangediary.member.ui.dto.response;

import lombok.Builder;

@Builder
public record MemberIdResponse(
        Long memberId
) {
    public static MemberIdResponse from(Long memberId) {
        return MemberIdResponse.builder()
                .memberId(memberId)
                .build();
    }
}
