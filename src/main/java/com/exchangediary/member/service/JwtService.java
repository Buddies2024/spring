package com.exchangediary.member.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.member.domain.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    public final String COOKIE_NAME = "token";
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.access-token.expiration-time}")
    private long accessTokenExpirationTime;
    @Value("${security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;
    private final RefreshTokenService refreshTokenService;

    public String generateAccessToken(Long memberId) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setSubject(String.valueOf(memberId))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String verifyAccessToken(String token) throws UnauthorizedException {
        try {
            verifyToken(token);
        } catch (ExpiredJwtException exception) {
            Long memberId = Long.valueOf(exception.getClaims().getSubject());
            verifyRefreshToken(memberId);
            return generateAccessToken(memberId);
        }
        return null;
    }

    public Long extractMemberId(String token) {
        try {
            String sub = extractAllClaims(token).getSubject();
            return Long.valueOf(sub);
        } catch (JwtException | IllegalArgumentException ignored) {}
        return null;
    }

    private void verifyRefreshToken(Long memberId) throws UnauthorizedException{
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByMemberId(memberId);

        try {
            verifyToken(refreshToken.getToken());
        } catch (JwtException exception) {
            refreshTokenService.expireRefreshToken(refreshToken);
            throw new UnauthorizedException(
                    ErrorCode.EXPIRED_TOKEN,
                    "",
                    refreshToken.getToken()
            );
        }
    }

    private void verifyToken(String token) {
        try {
            extractAllClaims(token);
        } catch (ExpiredJwtException exception) {
            throw exception;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException(
                    ErrorCode.JWT_TOKEN_UNAUTHORIZED,
                    "",
                    token
            );
        }
    }

    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
