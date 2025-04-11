package com.exchangediary.group.service;

import com.exchangediary.diary.service.DiaryImageService;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.notification.GroupLeaveNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupLeaveService {
    private final DiaryImageService diaryImageService;
    private final GroupQueryService groupQueryService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupLeaveNotification leaveGroup(String groupId, Long memberId) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember leaveMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);

        forbidGroupLeaderLeave(leaveMember, group.getMemberCount());
        diaryImageService.deleteAllImageByMemberId(memberId, groupId);

        groupMemberRepository.delete(leaveMember);
        groupMemberRepository.flush();

        updateGroupAfterMemberLeave(group, leaveMember.getOrderInGroup());
        return GroupLeaveNotification.from(leaveMember);
    }

    private void forbidGroupLeaderLeave(GroupMember groupMember, int memberCount) {
        if (memberCount > 1 && GroupRole.GROUP_LEADER.equals(groupMember.getGroupRole())) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_LEAVE_FORBIDDEN, "", "");
        }
    }

    private void updateGroupAfterMemberLeave(Group group, int leaveMembersOrder) {
        if (group.getMemberCount() == 1) {
            groupRepository.delete(group);
        } else {
            group.leaveMember();
            updateGroupCurrentOrder(group, leaveMembersOrder);
            updateGroupMembersOrder(group, leaveMembersOrder);
        }
    }

    private void updateGroupCurrentOrder(Group group, int leaveMembersOrder) {
        if (leaveMembersOrder < group.getCurrentOrder()) {
            group.changeCurrentOrder(group.getCurrentOrder() - 1);
        } else if (leaveMembersOrder == group.getCurrentOrder()) {
            group.changeCurrentOrder(group.getCurrentOrder());
            group.getGroupMembers().get(group.getCurrentOrder() - 1).updateLastViewableDiaryDate();
        }
    }

    private void updateGroupMembersOrder(Group group, int leaveMembersOrder) {
        group.getGroupMembers().stream()
                .filter(member -> member.getOrderInGroup() > leaveMembersOrder)
                .forEach(member -> member.changeOrderInGroup(member.getOrderInGroup() - 1));
    }
}
