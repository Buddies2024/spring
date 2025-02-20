package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupMemberFindService {
    public GroupMember findSelfInGroup(Group group, Long memberId) {
        return group.getGroupMembers().stream()
                .filter(member -> memberId.equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "",
                        String.valueOf(memberId)
                ));
    }

    public GroupMember findGroupLeader(Group group) {
        return group.getGroupMembers().stream()
                .filter(member -> GroupRole.GROUP_LEADER.equals(member.getGroupRole()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.GROUP_LEADER_NOT_FOUND,
                        "",
                        String.valueOf(group.getId())
                ));
    }

    public GroupMember findMemberByNickname(Group group, String nickname) {
        return group.getGroupMembers().stream()
                .filter(member -> member.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "",
                        nickname
                ));
    }

    public GroupMember findCurrentOrderMember(Group group) {
        return group.getGroupMembers().stream()
                .filter(member -> group.getCurrentOrder().equals(member.getOrderInGroup()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "",
                        String.valueOf(group.getCurrentOrder())
                ));
    }
}
