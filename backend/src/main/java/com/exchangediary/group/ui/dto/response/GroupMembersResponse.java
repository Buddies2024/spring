package com.exchangediary.group.ui.dto.response;

import com.exchangediary.group.domain.entity.GroupMember;
import lombok.Builder;

import java.util.List;

@Builder
public record GroupMembersResponse(
        List<GroupMemberResponse> members,
        int selfIndex,
        int leaderIndex,
        int currentWriterIndex
) {
    public static GroupMembersResponse of(
            List<GroupMember> groupMembers,
            int selfIndex,
            int leaderIndex,
            int currentWriterIndex
    ) {
        List<GroupMemberResponse> groupMemberResponses = groupMembers.stream()
                .map(GroupMemberResponse::from)
                .toList();
        return GroupMembersResponse.builder()
                .members(groupMemberResponses)
                .selfIndex(selfIndex)
                .leaderIndex(leaderIndex)
                .currentWriterIndex(currentWriterIndex)
                .build();
    }

    @Builder
    public record GroupMemberResponse(
            String nickname,
            String profileImage
    ) {
        public static GroupMemberResponse from(GroupMember groupMember) {
            return GroupMemberResponse.builder()
                    .nickname(groupMember.getNickname())
                    .profileImage(groupMember.getProfileImage())
                    .build();
        }
    }
}
