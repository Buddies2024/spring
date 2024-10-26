package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.response.GroupNicknameVerifyResponse;
import com.exchangediary.group.ui.dto.response.GroupMembersResponse;
import com.exchangediary.group.ui.dto.response.GroupProfileResponse;
import com.exchangediary.group.ui.dto.response.GroupMonthlyResponse;
import com.exchangediary.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryService {
    private final GroupValidationService groupValidationService;
    private final GroupMemberService groupMemberService;
    private final GroupRepository groupRepository;

    public GroupProfileResponse viewSelectableProfileImage(Long groupId) {
        Group group = findGroup(groupId);
        List<Member> members = group.getMembers();
        return GroupProfileResponse.from(members);
    }

    public GroupNicknameVerifyResponse verifyNickname(Long groupId, String nickname) {
        Group group = findGroup(groupId);
        groupValidationService.checkNicknameDuplicate(group.getMembers(), nickname);
        return GroupNicknameVerifyResponse.from(true);
    }

    public Group findGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.GROUP_NOT_FOUND,
                        "",
                        String.valueOf(groupId)
                ));
    }

    public GroupMonthlyResponse getGroupMonthlyInfo(Long groupId) {
        Group group = findGroup(groupId);
        return GroupMonthlyResponse.of(group);
    }

    public GroupMembersResponse listGroupMembersByOrder(Long memberId, Long groupId) {
        Group group = findGroup(groupId);
        Member self = groupMemberService.findSelfInGroup(group, memberId);
        Member leader = groupMemberService.findGroupLeader(group);
        return GroupMembersResponse.of(
                group.getMembers(),
                self.getOrderInGroup() - 1,
                leader.getOrderInGroup() - 1,
                group.getCurrentOrder() - 1
        );
    }

    public boolean isMyOrderInGroup(Long memberId) {
        return groupRepository.isEqualsToGroupCurrentOrder(memberId);
    }
}
