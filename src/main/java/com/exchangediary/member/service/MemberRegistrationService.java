package com.exchangediary.member.service;

import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.ui.dto.response.MemberIdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberRegistrationService {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    public MemberIdResponse getOrCreateMember(Long kakaoId) {
        Member member = memberRepository.findBykakaoId(kakaoId)
                .orElseGet(() -> signUp(kakaoId));
        jwtService.issueRefreshToken(member);
        return MemberIdResponse.from(member.getId());
    }

    private Member signUp(Long kakaoId) {
        Member newMember = Member.from(kakaoId);
        return memberRepository.save(newMember);
    }
}
