package com.exchangediary.member.service;

import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.member.ui.dto.response.AnonymousInfoResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnonymousService {
    private final CookieService cookieService;
    private final JwtService jwtService;
    private final MemberQueryService memberQueryService;

    public AnonymousInfoResponse viewAnonymousInfo(String token, HttpServletResponse response) {
        String groupId = null;
        boolean shouldLogin = needLogin(token, response);

        if (!shouldLogin) {
            Long memberId = jwtService.extractMemberId(token);
            groupId = memberQueryService.findGroupBelongTo(memberId).orElse(null);
        }
        return AnonymousInfoResponse.of(shouldLogin, groupId);
    }

    private boolean needLogin(String token, HttpServletResponse response) {
        try {
            token = jwtService.verifyAccessToken(token);

            if (token != null) {
                cookieService.addCookie(jwtService.COOKIE_NAME, token, response);
            }
        } catch (UnauthorizedException exception) {
            return true;
        }
        return false;
    }
}
