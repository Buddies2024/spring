package com.exchangediary.member.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.member.domain.RefreshTokenRepository;
import com.exchangediary.member.domain.entity.RefreshToken;
import com.exchangediary.member.service.KakaoService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LoginApiTest extends ApiBaseTest {
    private static final String URI = "/api/kakao/callback?code=%s";
    @MockBean
    private KakaoService kakaoService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("refresh token을 갖고 있지 않은 기존 회원이 카카오 로그인 성공하면 jwt refresh token이 발급된다.")
    void When_CurrentMemberNotHasRefreshTokenSuccessLogin_Expect_IssueRefreshToken() {
        String mockCode = "randomCode";

        when(kakaoService.loginKakao(any(String.class))).thenReturn(kakaoId);

        var response = RestAssured
                .given().log().all()
                .redirects().follow(false)
                .when().get(String.format(URI, mockCode))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FOUND.value())
                .extract();

        String token = response.cookie("token");
        Long memberId = jwtService.extractMemberId(token);
        assertThat(memberId).isEqualTo(member.getId());
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId).get();
        assertThat(refreshToken).isNotNull();
    }

    @Test
    @DisplayName("refresh token을 가진 기존 회원이 카카오 로그인 성공하면 jwt refresh token이 재발급된다.")
    void When_CurrentMemberHasRefreshTokenSuccessLogin_Expect_ReissueRefreshToken() {
        String mockCode = "randomCode";
        RefreshToken refreshToken = RefreshToken.of("refresh-token", member);
        refreshTokenRepository.save(refreshToken);

        when(kakaoService.loginKakao(any(String.class))).thenReturn(kakaoId);

        var response = RestAssured
                .given().log().all()
                .redirects().follow(false)
                .when().get(String.format(URI, mockCode))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FOUND.value())
                .extract();

        String token = response.cookie("token");
        Long memberId = jwtService.extractMemberId(token);
        assertThat(memberId).isEqualTo(member.getId());
        RefreshToken issuedRefreshToken = refreshTokenRepository.findByMemberId(memberId).get();
        assertThat(issuedRefreshToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("신규 회원이 카카오 로그인 성공하면 jwt token이 발급된다.")
    void When_NewMemberSuccessLogin_Expect_IssueRefreshToken() {
        String mockCode = "randomCode";
        Long mockKakaoId = 2L;

        when(kakaoService.loginKakao(any(String.class))).thenReturn(mockKakaoId);

        var response = RestAssured
                .given().log().all()
                .redirects().follow(false)
                .when().get(String.format(URI, mockCode))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FOUND.value())
                .extract();

        String token = response.cookie("token");
        assertThat(token).isNotNull();

        Long memberId = jwtService.extractMemberId(token);
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId).get();
        assertThat(refreshToken).isNotNull();
    }
}
