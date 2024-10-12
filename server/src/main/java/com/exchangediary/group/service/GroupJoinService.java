package com.exchangediary.group.service;

import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupJoinService {
    private final GroupQueryService groupQueryService;
    private final GroupValidationService groupValidationService;
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;

    public void joinGroup(Long groupId, GroupJoinRequest request, Long memberId) {
        Group group = groupQueryService.findGroup(groupId);
        Member member = memberQueryService.findMember(memberId);

        List<Member> members = group.getMembers();
        groupValidationService.checkProfileDuplicate(members, request.profileLocation());
        groupValidationService.checkNumberOfMembers(members.size());
        int maxOrderInGroup = findMaxOrderInGroup(group.getMembers());
        member.updateMemberGroupInfo(
                request.nickname(), request.profileLocation(),maxOrderInGroup + 1, group);
        memberRepository.save(member);
    }

    private int findMaxOrderInGroup(List<Member> members) {
        return members.stream()
                .mapToInt(Member::getOrderInGroup)
                .max()
                .orElse(0);
    }
}
