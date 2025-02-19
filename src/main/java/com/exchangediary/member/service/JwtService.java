package com.exchangediary.member.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.member.domain.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
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
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.access-token.expiration-time}")
    private long accessTokenExpirationTime;
    @Value("${security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;
    private final RefreshTokenService refreshTokenService;

    public String generateAccessToken(Long memberId) {
        return buildToken(memberId);
    }

    public String generateRefreshToken() {
        return buildToken(null);
    }

    public void verifyAccessToken(String token) {
        verifyToken(token);
    }

    public void verifyRefreshToken(Long memberId) {
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByMemberId(memberId);

        try {
            verifyToken(refreshToken.getToken());
        } catch (ExpiredJwtException exception) {
            refreshTokenService.expireRefreshToken(refreshToken);
            throw new UnauthorizedException(ErrorCode.EXPIRED_TOKEN,
                    "",
                    refreshToken.getToken()
            );
        }
    }

    public Long extractMemberId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    private String buildToken(Long memberId) {
        Date now = new Date(System.currentTimeMillis());
        long expirationTime = refreshTokenExpirationTime;
        if (memberId != null) {
            expirationTime = accessTokenExpirationTime;
        }
        Date expiration = new Date(now.getTime() + expirationTime);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        if (memberId != null) {
            jwtBuilder.setSubject(String.valueOf(memberId));
        }
        return jwtBuilder.compact();
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

    private Claims extractAllClaims(String token) {
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
