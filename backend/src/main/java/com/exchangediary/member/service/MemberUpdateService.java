package com.exchangediary.member.service;

import com.exchangediary.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberUpdateService {
    private final MemberQueryService memberQueryService;

    public void changeOnNotification(Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        member.toggleNotification();
    }
}
