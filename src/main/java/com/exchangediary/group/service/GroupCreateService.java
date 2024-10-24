package com.exchangediary.group.service;

import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupCreateRequest;
import com.exchangediary.group.ui.dto.response.GroupCreateResponse;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCreateService {
    private final GroupCodeService groupCodeService;
    private final GroupRepository groupRepository;
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;

    public GroupCreateResponse createGroup(GroupCreateRequest request, Long memberId) {
        Group group = saveGroup(request.groupName());
        updateMember(memberId, request, group);
        return GroupCreateResponse.of(group);
    }

    private Group saveGroup(String groupName) {
        Group group = Group.of(groupName, groupCodeService.generateCode(groupName));
        return groupRepository.save(group);
    }

    private void updateMember(Long memberId, GroupCreateRequest request, Group group) {
        Member member = memberQueryService.findMember(memberId);
        member.joinGroup(request.nickname(), request.profileImage(), 1, GroupRole.GROUP_LEADER, group);
        memberRepository.save(member);
    }
}
