package com.exchangediary.global.config.web.interceptor;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.member.service.CookieService;
import com.exchangediary.member.service.JwtService;
import com.exchangediary.member.service.MemberQueryService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final MemberQueryService memberQueryService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        if (request.getRequestURI().equals("/login")) {
            try {
                String token = verifyJwtToken(request, response);
                Long memberId = jwtService.extractMemberId(token);
                checkMemberExists(memberId);
                request.setAttribute("memberId", memberId);
            } catch (UnauthorizedException exception) {
                return true;
            }
            response.sendRedirect("/");
            return false;
        }

        try {
            String token = verifyJwtToken(request, response);
            Long memberId = jwtService.extractMemberId(token);
            checkMemberExists(memberId);
            request.setAttribute("memberId", memberId);
        } catch (UnauthorizedException exception) {
            if (request.getRequestURI().startsWith("/api")) {
                throw exception;
            }
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    private String verifyJwtToken(HttpServletRequest request,
                                HttpServletResponse response
    ) throws UnauthorizedException {
        String token = getJwtTokenFromCookies(request);
        Optional<Long> memberId = jwtService.verifyAccessToken(token);

        if (memberId.isPresent()) {
            String newToken = jwtService.generateAccessToken(memberId.get());
            cookieService.addCookie(jwtService.COOKIE_NAME, newToken, response);
            return newToken;
        }
        return token;
    }

    private String getJwtTokenFromCookies(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();

            return cookieService.getValueFromCookies(cookies, jwtService.COOKIE_NAME);
        } catch (RuntimeException exception) {
            throw new UnauthorizedException(
                    ErrorCode.NEED_TO_REQUEST_TOKEN,
                    "",
                    jwtService.COOKIE_NAME
            );
        }
    }

    private void checkMemberExists(Long memberId) {
        if (!memberQueryService.existMember(memberId)) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_EXIST_MEMBER_TOKEN,
                    "",
                    String.valueOf(memberId)
            );
        }
    }
}
