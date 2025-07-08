package com.exchangediary.comment.ui.dto.response;

import com.exchangediary.comment.domain.entity.Reply;
import lombok.Builder;

import java.util.List;

@Builder
public record ReplyResponse(
        String profileImage,
        String content
) {
    public static List<ReplyResponse> fromReplies(List<Reply> replies) {
        return replies.stream()
                .map(ReplyResponse::from)
                .toList();
    }

    private static ReplyResponse from(Reply reply) {
        return ReplyResponse.builder()
                .profileImage(reply.getGroupMember().getProfileImage())
                .content(reply.getContent())
                .build();
    }
}
