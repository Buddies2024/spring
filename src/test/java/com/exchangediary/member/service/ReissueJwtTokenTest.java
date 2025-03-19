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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "security.jwt.access-token.expiration-time=1000",
})
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ReissueJwtTokenTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @DisplayName("expire access token and valid refresh token")
    @Test
    void 액세스_토큰_재발급_확인() throws InterruptedException {
        Member member = Member.of(1L);
        memberRepository.save(member);
        String token = jwtService.generateAccessToken(member.getId());
        RefreshToken refreshToken = RefreshToken.of(jwtService.generateRefreshToken(),member);
        refreshTokenRepository.save(refreshToken);

        Thread.sleep(1000);
        Optional<Long> memberId = jwtService.verifyAccessToken(token);

        assertThat(memberId.isPresent()).isTrue();
    }
}
