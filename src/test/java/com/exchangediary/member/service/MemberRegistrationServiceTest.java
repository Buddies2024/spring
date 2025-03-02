package com.exchangediary.member.service;

import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberRegistrationServiceTest {
    @InjectMocks
    private MemberRegistrationService memberRegistrationService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JwtService jwtService;

    @Test
    @DisplayName("kakao id에 일치하는 회원이 없으면, 신규 회원을 생성한다.")
    void When_NoMemberMatchingKakaoId_Expect_CreateNewMember() {
        Long kakaoId = 1L;
        Member newMember = Member.from(kakaoId);

        when(memberRepository.findBykakaoId(kakaoId)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        Long result = memberRegistrationService.getOrCreateMember(kakaoId).memberId();

        assertThat(result).isEqualTo(newMember.getId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("kakao id에 일치하는 회원이 있으면, 기존 회원을 반환한다.")
    void When_MemberMatchingKakaoId_Expect_ReturnCurrentMember() {
        Long kakaoId = 1L;
        Member member = Member.from(kakaoId);

        when(memberRepository.findBykakaoId(kakaoId)).thenReturn(Optional.ofNullable(member));

        Long result = memberRegistrationService.getOrCreateMember(kakaoId).memberId();

        assertThat(result).isEqualTo(member.getId());
        verify(memberRepository, times(0)).save(any(Member.class));
    }
}
