package com.exchangediary.member.service;

import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.RefreshTokenRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.entity.RefreshToken;
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
    private final RefreshTokenRepository refreshTokenRepository;

    public MemberIdResponse getOrCreateMember(Long kakaoId) {
        Member member = memberRepository.findBykakaoId(kakaoId)
                .orElseGet(() -> signUp(kakaoId));
        issueRefreshToken(member);
        return MemberIdResponse.from(member.getId());
    }

    private Member signUp(Long kakaoId) {
        Member newMember = Member.of(kakaoId);
        return memberRepository.save(newMember);
    }

    private void issueRefreshToken(Member member) {
        String token = jwtService.generateRefreshToken();

        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        refreshToken -> {
                            refreshToken.reissueToken(token);
                            refreshTokenRepository.save(refreshToken);
                        },
                        () -> {
                            RefreshToken refreshToken = RefreshToken.of(token, member);
                            refreshTokenRepository.save(refreshToken);
                        }
                );
    }
}
