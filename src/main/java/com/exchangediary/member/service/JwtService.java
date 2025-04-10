package com.exchangediary.member.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.member.domain.RefreshTokenRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

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
    private final RefreshTokenRepository refreshTokenRepository;

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

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public Optional<Long> verifyAccessToken(String token) throws UnauthorizedException {
        try {
            verifyToken(token);
        } catch (ExpiredJwtException exception) {
            Long memberId = Long.valueOf(exception.getClaims().getSubject());
            verifyRefreshToken(memberId);
            return Optional.of(memberId);
        }
        return Optional.empty();
    }

    public Long extractMemberId(String token) {
        try {
            String sub = extractAllClaims(token).getSubject();
            return Long.valueOf(sub);
        } catch (JwtException | IllegalArgumentException ignored) {}
        return null;
    }

    @Transactional
    public void issueRefreshToken(Member member) {
        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        refreshToken -> {
                            refreshToken.reissueToken(generateRefreshToken());
                        },
                        () -> {
                            RefreshToken refreshToken = RefreshToken.of(generateRefreshToken(), member);
                            refreshTokenRepository.save(refreshToken);
                        }
                );
    }

    private void verifyRefreshToken(Long memberId) throws UnauthorizedException{
        RefreshToken refreshToken = findRefreshTokenByMemberId(memberId);

        try {
            verifyToken(refreshToken.getToken());
        } catch (JwtException exception) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException(
                    ErrorCode.EXPIRED_TOKEN,
                    "",
                    refreshToken.getToken()
            );
        }
    }

    private RefreshToken findRefreshTokenByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorCode.JWT_TOKEN_UNAUTHORIZED,
                        "",
                        String.valueOf(memberId)
                ));
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
