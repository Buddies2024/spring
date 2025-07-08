package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.notification.GroupJoinNotification;
import com.exchangediary.group.ui.dto.request.GroupCreateRequest;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
import com.exchangediary.group.ui.dto.response.GroupCreateResponse;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Group group = Group.from(request.groupName());

        Group savedGroup = groupRepository.save(group);
        createGroupMember(request.nickname(), request.profileImage(), GroupRole.GROUP_LEADER, savedGroup, member);
        return GroupCreateResponse.from(savedGroup);
    }

    public GroupJoinNotification joinGroup(String groupId, Long memberId, GroupJoinRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        Member member = memberQueryService.findMember(memberId);

        groupValidationService.checkNumberOfGroupMembers(group.getMemberCount());
        groupValidationService.checkNicknameDuplicate(group.getGroupMembers(), request.nickname());
        groupValidationService.checkProfileDuplicate(group.getGroupMembers(), request.profileImage());

        GroupMember joinMember = createGroupMember(request.nickname(), request.profileImage(), GroupRole.GROUP_MEMBER, group, member);
        return GroupJoinNotification.from(joinMember);
    }

    private GroupMember createGroupMember(String nickname, String profileImage, GroupRole groupRole, Group group, Member member) {
        GroupMember groupMember = GroupMember.of(
                nickname,
                profileImage,
                group.getMemberCount() + 1,
                groupRole,
                group,
                member
        );
        group.joinMember();
        return groupMemberRepository.save(groupMember);
    }
}
