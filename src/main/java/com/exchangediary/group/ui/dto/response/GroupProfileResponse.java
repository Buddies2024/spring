package com.exchangediary.group.ui.dto.response;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

import java.util.List;

@Builder
public record GroupProfileResponse(
        List<ImageResponse> selectedImages
) {
    public static GroupProfileResponse from(List<GroupMember> groupMembers) {
        List<ImageResponse> imageResponses = groupMembers.stream()
                .map(groupMember -> ImageResponse.from(groupMember.getProfileImage()))
                .toList();
        return GroupProfileResponse.builder()
                .selectedImages(imageResponses)
                .build();
    }

    @Builder
    private record ImageResponse(
            String profileImage
    ) {
        public static ImageResponse from(String profileImage) {
            return ImageResponse.builder()
                    .profileImage(profileImage)
                    .build();
        }
    }
}
