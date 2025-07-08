package com.exchangediary.comment.ui.dto.response;

import lombok.Builder;

@Builder
public record CommentCreationVerifyResponse(
        String profileImage
) {
    public static CommentCreationVerifyResponse from(String profileImage) {
        return CommentCreationVerifyResponse.builder()
                .profileImage(profileImage)
                .build();
    }
}
