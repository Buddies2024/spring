package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.member.domain.RefreshTokenRepository;
import com.exchangediary.member.domain.entity.RefreshToken;
import com.exchangediary.member.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtAuthenticationInterceptorTest extends ApiBaseTest {
    private final String URI = "/groups/%s";
    private final String LOGIN_URI = "/login";
    private final String API_URI = "/api/groups/%s/profile-image";
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private String groupId;

    @BeforeEach
    void setup() {
        this.groupId = createGroup().getId();
    }

    @Test
    @DisplayName("쿠키에 토큰이 없으면 로그인 페이지로 리다이렉트된다.")
    void When_HasNoTokenInCookie_Expect_RedirectLoginPage() {
        String location = RestAssured
                .given().log().all()
                .redirects().follow(false)
                .when().get(String.format(URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.FOUND.value())
                .extract()
                .header("Location");

        assertThat(location.substring(location.lastIndexOf("/"))).isEqualTo(LOGIN_URI);
    }

    @Test
    @DisplayName("쿠키에 유효한 토큰이 있는 경우 로그인 페이지 접근 시 시작 페이지로 리다이렉트된다.")
    void When_RequestLoginPageAndHasTokenInCookie_Expect_RedirectStartPage() {
        String location = RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(LOGIN_URI)
                .then().log().all()
                .statusCode(HttpStatus.FOUND.value())
                .extract()
                .header("Location");

        assertThat(location.substring(location.lastIndexOf("/"))).isEqualTo("/");
    }

    @Test
    @DisplayName("로그인 페이지 접근 시 쿠키에 토큰이 없는 경우 성공한다.")
    void When_RequestLoginPageAndHasNoToken_Expect_Sucess() {
        RestAssured
            .given().log().all()
            .redirects().follow(false)
            .when().get(LOGIN_URI)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("API 요청 시 쿠키에 토큰이 없으면 401 예외를 발생한다.")
    void When_RequestApiAndHasNoTokenInCookie_Expect_Throw401Exception() {
        RestAssured
                .given().log().all()
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-token"})
    @DisplayName("API 요청 시 쿠키에 잘못된 값의 토큰이 있으면 401 예외를 발생한다.")
    void When_RequestApiAndHasInvalidTokenInCookie_Expect_Throw401Exception(String token) {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("API 요청 시 토큰으로부터 추출한 사용자가 존재하지 않으면 401 예외를 발생한다.")
    void When_RequestApiAndHasNonExistentMemberToken_Expect_Throw401Exception() {
        String invalidToken = jwtService.generateAccessToken(1234L);

        RestAssured
                .given().log().all()
                .cookie("token", invalidToken)
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("API 요청 시 유효한 토큰을 가지고 있으면, API 요청에 성공한다.")
    void When_RequestApiAndHasValidToken_Expect_Success() {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("API 요청 시 만료된 access 토큰과 유효한 refresh 토큰을 가지고 있으면, API 요청에 성공하고 access 토큰을 재발급한다.")
    void When_RequestApiAndHasExpiredAccessTokenAndValidRefreshToken_Expect_ReissueAccessToken() {
        this.token = buildExpiredAccessToken();
        RefreshToken refreshToken = RefreshToken.of(jwtService.generateRefreshToken(), this.member);
        refreshTokenRepository.save(refreshToken);

        String token = RestAssured
                .given().log().all()
                .cookie("token", this.token)
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .cookie("token");

        Long memberId = jwtService.extractMemberId(token);
        assertThat(memberId).isEqualTo(this.member.getId());
    }

    @Test
    @DisplayName("API 요청 시 만료된 access 토큰을 가지고 있고 refresh 토큰이 없으면, 401 예외를 발생한다.")
    void When_RequestApiAndHasExpiredAccessTokenAndNoHasRefreshToken_Expect_Throw401Exception() {
        this.token = buildExpiredAccessToken();

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("API 요청 시 만료된 access 토큰과 refresh 토큰을 가지고 있으면, 401 예외를 발생한다.")
    void When_RequestApiAndHasExpiredAccessTokenAndRefreshToken_Expect_Throw401Exception() {
        this.token = buildExpiredAccessToken();
        RefreshToken refreshToken = RefreshToken.of(buildExpiredRefreshToken(), this.member);
        refreshTokenRepository.save(refreshToken);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        Optional<RefreshToken> result = refreshTokenRepository.findByMemberId(member.getId());
        assertThat(result.isEmpty()).isTrue();
    }

    private String buildExpiredAccessToken() {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() - 1000);

        return Jwts
                .builder()
                .setSubject(String.valueOf(this.member.getId()))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildExpiredRefreshToken() {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() - 1000);

        return Jwts
                .builder()
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
