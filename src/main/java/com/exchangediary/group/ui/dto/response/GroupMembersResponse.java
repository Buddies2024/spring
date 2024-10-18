package com.exchangediary.group.ui.dto.response;

import com.exchangediary.member.domain.entity.Member;
import lombok.Builder;

import java.util.List;

@Builder
public record GroupMembersResponse(
        List<GroupMemberResponse> members
) {
    public static GroupMembersResponse of(List<Member> members, Member self) {
        List<GroupMemberResponse> response = members.stream()
                .map(member -> GroupMemberResponse.of(member, self))
                .toList();
        return GroupMembersResponse.builder()
                .members(response)
                .build();
    }

    @Builder
    public record GroupMemberResponse(
            String nickname,
            String profileImage,
            boolean isSelf
    ) {
        public static GroupMemberResponse of(Member member, Member self) {
            return GroupMemberResponse.builder()
                    .nickname(member.getNickname())
                    .profileImage(member.getProfileImage())
                    .isSelf(member.equals(self))
                    .build();
        }
    }
}
