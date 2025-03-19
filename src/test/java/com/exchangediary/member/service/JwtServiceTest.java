package com.exchangediary.member.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    @Test
    void 토큰_발급_성공() {
        Long memberId = 1L;
        String token = jwtService.generateAccessToken(memberId);

        Long result = jwtService.extractMemberId(token);

        assertThat(result).isEqualTo(memberId);
    }

    @Test
    void 유효한_토큰_검증() {
        Long memberId = 1L;
        String token = jwtService.generateAccessToken(memberId);

        Optional<Long> result = jwtService.verifyAccessToken(token);

        assertThat(result.isPresent()).isFalse();
    }
}
