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
        boolean shouldLogin = true;
        String groupId = null;

        try {
            token = jwtService.verifyAccessToken(token);

            if (token != null) {
                cookieService.addCookie(jwtService.COOKIE_NAME, token, response);
            }
            shouldLogin = false;
            Long memberId = jwtService.extractMemberId(token);
            groupId = memberQueryService.findGroupIdBelongTo(memberId).orElse(null);
        } catch (UnauthorizedException ignored) {}

        return AnonymousInfoResponse.of(shouldLogin, groupId);
    }
}
