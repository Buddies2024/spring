package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.ui.dto.notification.GroupLeaderHandOverNotification;
import com.exchangediary.group.ui.dto.notification.GroupLeaderKickOutNotification;
import com.exchangediary.group.ui.dto.notification.GroupLeaderSkipDiaryNotification;
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

    public GroupLeaderHandOverNotification handOverGroupLeader(String groupId, Long memberId, GroupLeaderHandOverRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember currentLeader = groupMemberFindService.findSelfInGroup(group, memberId);
        GroupMember newLeader = groupMemberFindService.findMemberByNickname(group, request.nickname());

        currentLeader.changeGroupRole(GroupRole.GROUP_MEMBER);
        newLeader.changeGroupRole(GroupRole.GROUP_LEADER);
        return GroupLeaderHandOverNotification.of(currentLeader, newLeader);
    }

    public GroupLeaderSkipDiaryNotification skipDiaryOrder(String groupId) {
        Group group = groupQueryService.findGroup(groupId);
        groupValidationService.checkSkipOrderAuthority(group);

        GroupMember skipMember = groupMemberFindService.findCurrentOrderMember(group);

        group.changeCurrentOrder(group.getCurrentOrder() + 1);
        group.updateLastSkipOrderDate();
        GroupMember currentWriter = groupMemberFindService.findCurrentOrderMember(group);
        currentWriter.updateLastViewableDiaryDate();

        return GroupLeaderSkipDiaryNotification.from(skipMember);
    }

    public GroupLeaderKickOutNotification kickOutMember(String groupId, GroupKickOutRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember kickMember = groupMemberFindService.findMemberByNickname(group, request.nickname());
        groupLeaveService.leaveGroup(groupId, kickMember.getMember().getId());
        return GroupLeaderKickOutNotification.from(kickMember);
    }

    public boolean isGroupLeader(Long memberId) {
        return groupMemberRepository.isGroupLeaderByMemberId(memberId);
    }
}
