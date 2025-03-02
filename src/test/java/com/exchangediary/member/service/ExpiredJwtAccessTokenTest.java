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
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "security.jwt.access-token.expiration-time=1000",
})
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ExpiredJwtAccessTokenTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("만료된 access token을 검증 시 사용자의 refresh token의 유효하다면, access token을 재발급한다.")
    void When_ExpiredJwtAccessTokenAndValidRefreshToken_Then_ReissueAccessToken() throws InterruptedException {
        // Given
        Member member = Member.from(1L);
        memberRepository.save(member);

        String token = jwtService.generateAccessToken(member.getId());
        RefreshToken refreshToken = RefreshToken.of(jwtService.generateRefreshToken(),member);
        refreshTokenRepository.save(refreshToken);

        Thread.sleep(1000);

        // When
        String newToken = jwtService.verifyAccessToken(token);

        // Then
        Long memberId = jwtService.extractMemberId(newToken);
        assertThat(memberId).isEqualTo(member.getId());
    }
}
