package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupLeaderHandOverRequest;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupLeaderService {
    private final GroupQueryService groupQueryService;

    public void handOverGroupLeader(Long groupId, Long memberId, GroupLeaderHandOverRequest request) {
        Group group = groupQueryService.findGroup(groupId);
        Member currentLeader = groupQueryService.findSelfInGroup(group, memberId);
        Member newLeader = findGroupMemberByIndex(group, request.nextLeaderIndex());

        currentLeader.changeGroupRole(GroupRole.GROUP_MEMBER);
        newLeader.changeGroupRole(GroupRole.GROUP_LEADER);
    }

    private Member findGroupMemberByIndex(Group group, int index) {
        try {
            return group.getMembers().get(index);
        } catch (RuntimeException exception) {
            throw new NotFoundException(
                    ErrorCode.MEMBER_NOT_FOUND,
                    "",
                    String.valueOf(index)
            );
        }
    }
}
