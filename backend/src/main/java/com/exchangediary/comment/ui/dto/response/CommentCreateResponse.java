package com.exchangediary.comment.ui.dto.response;

import com.exchangediary.comment.domain.entity.Comment;
import lombok.Builder;

@Builder
public record CommentCreateResponse(
        Long id,
        Integer page,
        String profileImage,
        Double xCoordinate,
        Double yCoordinate
) {
    public static CommentCreateResponse of(Comment comment, String profileImage) {
        return CommentCreateResponse.builder()
                .id(comment.getId())
                .page(comment.getPage())
                .profileImage(profileImage)
                .xCoordinate(comment.getXCoordinate())
                .yCoordinate(comment.getYCoordinate())
                .build();
    }
}
