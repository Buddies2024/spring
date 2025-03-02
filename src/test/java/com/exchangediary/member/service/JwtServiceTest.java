package com.exchangediary.member.service;

import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.RefreshTokenRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Jwt access token을 발급한다.")
    void Expect_IssueJwtAccessToken() {
        Long memberId = 1L;
        String token = jwtService.generateAccessToken(memberId);

        Long result = jwtService.extractMemberId(token);

        assertThat(result).isEqualTo(memberId);
    }

    @Test
    @DisplayName("유효한 jwt access token을 검증한다.")
    void When_VerifyValidJwtAccessToken_Expect_ReturnNull() {
        Long memberId = 1L;
        String token = jwtService.generateAccessToken(memberId);

        String newToken = jwtService.verifyAccessToken(token);

        assertThat(newToken).isNull();
    }

    @Test
    @DisplayName("기존 회원의 jwt refresh token을 재발급하다.")
    @Sql(scripts = {"classpath:truncate.sql"})
    void When_CurrentMember_Expect_ReissueRefreshToken() {
        Member member = Member.from(1L);
        memberRepository.save(member);
        RefreshToken refreshToken = RefreshToken.of("token", member);
        refreshTokenRepository.save(refreshToken);

        jwtService.issueRefreshToken(member);

        RefreshToken issuedToken = refreshTokenRepository.findByMemberId(member.getId()).get();
        assertThat(issuedToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("기존 회원의 jwt refresh token을 발급하다.")
    @Sql(scripts = {"classpath:truncate.sql"})
    void When_CurrentMemberNotHasRefreshToken_Expect_IssueRefreshToken() {
        Member member = Member.from(1L);
        memberRepository.save(member);

        jwtService.issueRefreshToken(member);

        RefreshToken issuedToken = refreshTokenRepository.findByMemberId(member.getId()).get();
        assertThat(issuedToken).isNotNull();
    }

    @Test
    @DisplayName("신규 회원의 jwt refresh token을 발급하다.")
    @Sql(scripts = {"classpath:truncate.sql"})
    void When_NewMember_Expect_IssueRefreshToken() {
        Member member = Member.from(1L);
        memberRepository.save(member);

        jwtService.issueRefreshToken(member);

        RefreshToken issuedToken = refreshTokenRepository.findByMemberId(member.getId()).get();
        assertThat(issuedToken).isNotNull();
    }
}
