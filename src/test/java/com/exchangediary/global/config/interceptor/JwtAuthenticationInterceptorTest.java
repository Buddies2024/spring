package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
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
    private final String URI = "/api/groups/%s/profile-image";
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private GroupRepository groupRepository;
    private String groupId;

    @BeforeEach
    void createGroup() {
        Group group = Group.from("버니즈");
        groupRepository.save(group);
        this.groupId = group.getId();
    }

    @Test
    void 인증_실패_쿠키에_토큰없음() {
        RestAssured
                .given().log().all()
                .when().get(String.format(URI, groupId))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-token"})
    void 인증_실패_쿠키에_잘못된_토큰(String token) {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, groupId))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Valid access token. Then success authentication.")
    void 인증_성공_유효한_액세스_토큰() {
        RestAssured
                .given().log().all()
                .cookie("token", this.token)
                .when().get(String.format(URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Expired access token, and valid refresh token, so re-issue access token. Then success authentication.")
    void 인증_성공_액세스_토큰_재발급() {
        this.token = buildExpiredAccessToken();
        RefreshToken refreshToken = RefreshToken.of(jwtService.generateRefreshToken(), this.member);
        refreshTokenRepository.save(refreshToken);

        String token = RestAssured
                .given().log().all()
                .cookie("token", this.token)
                .when().get(String.format(URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .cookie("token");

        Long memberId = jwtService.extractMemberId(token);
        assertThat(memberId).isEqualTo(this.member.getId());
    }

    @Test
    @DisplayName("Expired access token, and member has no refresh token. Then fail authentication.")
    void 인증_실패_리프레쉬_토큰없음() {
        this.token = buildExpiredAccessToken();

        RestAssured
                .given().log().all()
                .cookie("token", this.token)
                .when().get(String.format(URI, groupId))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Expired access token, and expired refresh token. Then fail authentication.")
    void 인증_실패_만료된_리프레쉬_토큰() {
        this.token = buildExpiredAccessToken();
        RefreshToken refreshToken = RefreshToken.of(buildExpiredRefreshToken(), this.member);
        refreshTokenRepository.save(refreshToken);

        RestAssured
                .given().log().all()
                .cookie("token", this.token)
                .when().get(String.format(URI, groupId))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        Optional<RefreshToken> result = refreshTokenRepository.findByMemberId(member.getId());
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void 인증_실패_존재하지_않는_사용자의_토큰() {
        this.token = jwtService.generateAccessToken(this.member.getId() + 100L);

        RestAssured
                .given().log().all()
                .cookie("token", this.token)
                .when().get(String.format(URI, groupId))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
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
