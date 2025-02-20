package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.request.GroupCreateRequest;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
import com.exchangediary.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberCreateService {
    private final GroupMemberRepository groupMemberRepository;

    public GroupMember createGroupLeader(GroupCreateRequest request, Group group, Member member) {
        GroupMember groupMember = GroupMember.of(
                request.nickname(),
                request.profileImage(),
                1,
                GroupRole.GROUP_LEADER,
                group,
                member
        );
        return groupMemberRepository.save(groupMember);
    }

    public GroupMember createGroupMember(GroupJoinRequest request, Group group, Member member) {
        GroupMember groupMember = GroupMember.of(
                request.nickname(),
                request.profileImage(),
                group.getGroupMembers().size() + 1,
                GroupRole.GROUP_MEMBER,
                group,
                member
        );
        return groupMemberRepository.save(groupMember);
    }
}
