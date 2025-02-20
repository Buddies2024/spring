package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
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
    private final GroupMemberCreateService groupMemberCreateService;
    private final GroupRepository groupRepository;


    public GroupCreateResponse createGroup(GroupCreateRequest request, Long memberId) {
        Member member = memberQueryService.findMember(memberId);

        Group group = groupRepository.save(Group.from(request.groupName()));

        groupMemberCreateService.createGroupLeader(request, group, member);
        return GroupCreateResponse.from(group);
    }

    public void joinGroup(String groupId, Long memberId, GroupJoinRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        Member member = memberQueryService.findMember(memberId);

        List<GroupMember> groupMembers = group.getGroupMembers();

        groupValidationService.checkProfileDuplicate(groupMembers, request.profileImage());
        groupValidationService.checkNumberOfGroupMembers(groupMembers.size());

        groupMemberCreateService.createGroupMember(request, group, member);
    }
}
