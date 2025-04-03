package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.ui.dto.request.GroupKickOutRequest;
import com.exchangediary.group.ui.dto.request.GroupLeaderHandOverRequest;
import com.exchangediary.group.domain.enums.GroupRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupLeaderService {
    private final GroupQueryService groupQueryService;
    private final GroupLeaveService groupLeaveService;
    private final GroupMemberFindService groupMemberFindService;
    private final GroupValidationService groupValidationService;
    private final GroupMemberRepository groupMemberRepository;

    public long handOverGroupLeader(String groupId, Long memberId, GroupLeaderHandOverRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember currentLeader = groupMemberFindService.findSelfInGroup(group, memberId);
        GroupMember newLeader = groupMemberFindService.findMemberByNickname(group, request.nickname());

        currentLeader.changeGroupRole(GroupRole.GROUP_MEMBER);
        newLeader.changeGroupRole(GroupRole.GROUP_LEADER);
        return newLeader.getId();
    }

    public long skipDiaryOrder(String groupId) {
        Group group = groupQueryService.findGroup(groupId);
        groupValidationService.checkSkipOrderAuthority(group);

        long skipDiaryMemberId = groupMemberFindService.findCurrentOrderMember(group).getId();
        group.changeCurrentOrder(group.getCurrentOrder() + 1);
        group.updateLastSkipOrderDate();
        GroupMember currentWriter = groupMemberFindService.findCurrentOrderMember(group);
        currentWriter.updateLastViewableDiaryDate();
        return skipDiaryMemberId;
    }

    public long kickOutMember(String groupId, GroupKickOutRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember kickMember = groupMemberFindService.findMemberByNickname(group, request.nickname());
        groupLeaveService.leaveGroup(groupId, kickMember.getId());
        return kickMember.getId();
    }

    public boolean isGroupLeader(Long memberId) {
        return groupMemberRepository.isGroupLeaderByMemberId(memberId);
    }
}
