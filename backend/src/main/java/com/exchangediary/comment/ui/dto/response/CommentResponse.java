package com.exchangediary.comment.ui.dto.response;

import com.exchangediary.comment.domain.entity.Comment;
import lombok.Builder;

import java.util.List;

@Builder
public record CommentResponse(
        String content,
        String profileImage,
        List<ReplyResponse> replies
) {
    public static CommentResponse of(Comment comment, String profileImage) {
        return CommentResponse.builder()
                .content(comment.getContent())
                .profileImage(profileImage)
                .replies(ReplyResponse.fromReplies(comment.getReplies()))
                .build();
    }
}
