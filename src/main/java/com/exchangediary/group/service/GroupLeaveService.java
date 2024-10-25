package com.exchangediary.group.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupLeaveService {
    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;
    private final GroupQueryService groupQueryService;
    private final GroupRepository groupRepository;
    private final DiaryRepository diaryRepository;

    public void leaveGroup(Long groupId, Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        Group group = groupQueryService.findGroup(groupId);
        int orderInGroup = member.getOrderInGroup();

        checkGroupLeaderLeave(member);
        diaryRepository.deleteByMemberId(memberId);
        member.joinGroup(null, null, 0, null, null);
        memberRepository.save(member);
        updateGroupMembersOrder(group, orderInGroup);
        updateGroupCurrentOrder(group, orderInGroup);
        deleteGroup(group);
    }

    private void updateGroupMembersOrder(Group group, int orderInGroup) {
        group.getMembers().stream()
                .filter(member -> member.getOrderInGroup() > orderInGroup)
                .forEach(member -> member.updateOrderInGroup(member.getOrderInGroup() - 1));
        memberRepository.saveAll(group.getMembers());
    }

    private void updateGroupCurrentOrder(Group group, int orderInGroup) {
        List<Member> members = group.getMembers();
        int currentOrder = group.getCurrentOrder();

        if (orderInGroup < currentOrder) {
            group.updateCurrentOrder(currentOrder - 1, members.size());
        }
        else {
            group.updateCurrentOrder(currentOrder, members.size() - 1);
        }
        groupRepository.save(group);
    }

    private void deleteGroup(Group group) {
        int sizeMembersOfGroup = group.getMembers().size();
        if (sizeMembersOfGroup - 1 == 0) {
            groupRepository.delete(group);
        }
    }

    private void checkGroupLeaderLeave(Member member) {
        if (member.getGroupRole() == GroupRole.GROUP_LEADER) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_LEAVE_FORBIDDEN, "", "");
        }
    }
}
