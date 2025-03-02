package com.exchangediary.member.service;

import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.RefreshTokenRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "security.jwt.access-token.expiration-time=1000",
        "security.jwt.refresh-token.expiration-time=1000"
})
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ExpiredJwtRefreshTokenTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("만료된 access token을 검증 시 사용자가 refresh token을 갖고 있지 않다면, 예외를 던진다")
    void When_ExpiredJwtAccessTokenAndNoHasRefreshToken_Then_ThrowException() throws InterruptedException {
        Member member = Member.from(1L);
        memberRepository.save(member);
        String token = jwtService.generateAccessToken(member.getId());

        Thread.sleep(1000);
        assertThrows(UnauthorizedException.class, () ->
                jwtService.verifyAccessToken(token)
        );
    }

    @Test
    @DisplayName("만료된 access token을 검증 시 사용자의 refresh token도 만료됐다면, 예외를 던진다")
    void When_ExpiredJwtAccessTokenAndExpiredRefreshToken_Then_ThrowException() throws InterruptedException {
        Member member = Member.from(1L);
        memberRepository.save(member);
        String token = jwtService.generateAccessToken(member.getId());
        RefreshToken refreshToken = RefreshToken.of(jwtService.generateRefreshToken(), member);
        refreshTokenRepository.save(refreshToken);

        Thread.sleep(1000);
        assertThrows(UnauthorizedException.class, () ->
                jwtService.verifyAccessToken(token)
        );

        Optional<RefreshToken> result = refreshTokenRepository.findByMemberId(member.getId());
        assertThat(result.isEmpty()).isTrue();
    }
}
