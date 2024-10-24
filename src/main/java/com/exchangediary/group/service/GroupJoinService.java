package com.exchangediary.group.service;

import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
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
public class GroupJoinService {
    private final GroupQueryService groupQueryService;
    private final GroupValidationService groupValidationService;
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;

    public void joinGroup(Long groupId, GroupJoinRequest request, Long memberId) {
        Group group = groupQueryService.findGroup(groupId);
        Member member = memberQueryService.findMember(memberId);

        List<Member> members = group.getMembers();
        groupValidationService.checkProfileDuplicate(members, request.profileImage());
        groupValidationService.checkNumberOfMembers(members.size());

        member.joinGroup(
                request.nickname(),
                request.profileImage(),
                members.size() + 1,
                GroupRole.GROUP_MEMBER,
                group);
        memberRepository.save(member);
    }
}
