package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.request.GroupCreateRequest;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
import com.exchangediary.group.ui.dto.response.GroupCreateResponse;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCreateJoinService {
    private final MemberQueryService memberQueryService;
    private final GroupQueryService groupQueryService;
    private final GroupValidationService groupValidationService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupCreateResponse createGroup(GroupCreateRequest request, Long memberId) {
        Member member = memberQueryService.findMember(memberId);

        Group group = groupRepository.save(Group.from(request.groupName()));

        createGroupMember(request.nickname(), request.profileImage(), GroupRole.GROUP_LEADER, group, member);

        return GroupCreateResponse.from(group);
    }

    public void joinGroup(String groupId, Long memberId, GroupJoinRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        Member member = memberQueryService.findMember(memberId);

        List<GroupMember> groupMembers = group.getGroupMembers();

        groupValidationService.checkProfileDuplicate(groupMembers, request.profileImage());
        groupValidationService.checkNumberOfGroupMembers(groupMembers.size());

        createGroupMember(request.nickname(), request.profileImage(), GroupRole.GROUP_MEMBER, group, member);
    }

    private void createGroupMember(String nickname, String profileImage, GroupRole groupRole, Group group, Member member) {
        GroupMember groupMember = GroupMember.of(
                nickname,
                profileImage,
                group.getMemberCount() + 1,
                groupRole,
                group,
                member
        );
        groupMemberRepository.save(groupMember);
        group.joinMember();
    }
}
