package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.ui.dto.response.GroupMembersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberQueryService {
    private final GroupQueryService groupQueryService;
    private final GroupMemberFindService groupMemberFindService;
    private final GroupMemberRepository groupMemberRepository;

    public GroupMember findGroupMemberByMemberId(Long memberId) {
        return groupMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.GROUP_MEMBER_NOT_FOUND,
                        "",
                        String.valueOf(memberId)
                ));
    }

    public Optional<String> findGroupIdBelongTo(Long memberId) {
        return groupMemberRepository.findGroupIdByMemberId(memberId);
    }

    public LocalDate getLastViewableDiaryDate(Long memberId) {
        return groupMemberRepository.findLastViewableDiaryDateByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "",
                        String.valueOf(memberId)
                ));
    }

    public boolean isCurrentOrderInGroup(Long memberId) {
        return groupMemberRepository.isCurrentOrderByMemberId(memberId);
    }

    public GroupMembersResponse listGroupMembersInfo(Long memberId, String groupId) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember self = groupMemberFindService.findSelfInGroup(group, memberId);
        GroupMember leader = groupMemberFindService.findGroupLeader(group);

        return GroupMembersResponse.of(
                group.getGroupMembers(),
                self.getOrderInGroup() - 1,
                leader.getOrderInGroup() - 1,
                group.getCurrentOrder() - 1
        );
    }
}
