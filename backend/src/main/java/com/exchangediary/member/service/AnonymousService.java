package com.exchangediary.member.service;

import com.exchangediary.global.exception.serviceexception.UnauthorizedException;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.member.ui.dto.response.AnonymousInfoResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnonymousService {
    private final CookieService cookieService;
    private final JwtService jwtService;
    private final GroupMemberQueryService groupMemberQueryService;

    public AnonymousInfoResponse viewAnonymousInfo(String token, HttpServletResponse response) {
        token = needLogin(token, response);
        boolean shouldLogin = (token == null);
        String groupId = null;

        if (!shouldLogin) {
            Long memberId = jwtService.extractMemberId(token);
            groupId = groupMemberQueryService.findGroupIdBelongTo(memberId).orElse(null);
        }

        return AnonymousInfoResponse.of(shouldLogin, groupId);
    }

    private String needLogin(String token, HttpServletResponse response) {
        try {
            Optional<Long> memberId = jwtService.verifyAccessToken(token);

            if (memberId.isPresent()) {
                String newToken = jwtService.generateAccessToken(memberId.get());
                cookieService.addCookie(jwtService.COOKIE_NAME, newToken, response);
                token = newToken;
            }
            return token;
        } catch (UnauthorizedException exception) {
            return null;
        }
    }
}
