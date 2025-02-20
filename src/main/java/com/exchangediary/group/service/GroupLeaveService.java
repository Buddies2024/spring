package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupLeaveService {
    private final GroupQueryService groupQueryService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public void leaveGroup(String groupId, Long memberId) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember groupMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);

        forbidGroupLeaderLeave(groupMember, group.getGroupMembers().size());
        int leaveMemberOrder = groupMember.getOrderInGroup();
        groupMemberRepository.delete(groupMember);
        updateGroupAfterMemberLeave(group, leaveMemberOrder);
    }

    private void forbidGroupLeaderLeave(GroupMember groupMember, int numberOfGroupMember) {
        if (numberOfGroupMember > 1 && GroupRole.GROUP_LEADER.equals(groupMember.getGroupRole())) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_LEAVE_FORBIDDEN, "", "");
        }
    }

    private void updateGroupAfterMemberLeave(Group group, int leaveMemberOrder) {
        if (group.getGroupMembers().size() == 1) {
            groupRepository.delete(group);
        } else {
            updateGroupMembersOrder(group, leaveMemberOrder);
            updateGroupCurrentOrder(group, leaveMemberOrder);
        }
    }

    private void updateGroupMembersOrder(Group group, int leaveMemberOrder) {
        group.getGroupMembers().stream()
                .filter(member -> member.getOrderInGroup() > leaveMemberOrder)
                .forEach(member -> member.changeOrderInGroup(member.getOrderInGroup() - 1));
        groupMemberRepository.saveAll(group.getGroupMembers());
    }

    private void updateGroupCurrentOrder(Group group, int leaveMemberOrder) {
        List<GroupMember> groupMembers = group.getGroupMembers();
        int currentOrder = group.getCurrentOrder();
        int numberOfGroupMember = groupMembers.size() - 1;

        if (leaveMemberOrder < currentOrder) {
            group.updateCurrentOrder(currentOrder - 1, numberOfGroupMember);
        } else {
            group.updateCurrentOrder(currentOrder, numberOfGroupMember);
        }

        if (leaveMemberOrder == currentOrder) {
            int index = getCurrentMemberIndex(group.getCurrentOrder());
            group.getGroupMembers().get(index).updateLastViewableDiaryDate();
        }

        groupRepository.save(group);
    }

    private int getCurrentMemberIndex(int currentOrder) {
        if (currentOrder == 1) {
            return 0;
        }
        return currentOrder;
    }
}
