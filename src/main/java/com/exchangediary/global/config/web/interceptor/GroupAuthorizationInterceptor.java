package com.exchangediary.global.config.web.interceptor;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.member.service.MemberQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class GroupAuthorizationInterceptor implements HandlerInterceptor {
    private final MemberQueryService memberQueryService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String groupIdInUri = extractGroupId(pathVariables, request.getRequestURI());

        Long memberId = (Long) request.getAttribute("memberId");
        Optional<String> memberGroupId = memberQueryService.findGroupIdBelongTo(memberId);

        if (memberGroupId.isEmpty() || !memberGroupId.get().equals(groupIdInUri)) {
            throw new ForbiddenException(ErrorCode.GROUP_FORBIDDEN, "", groupIdInUri);
        }
        return true;
    }

    private String extractGroupId(Map<String, String> pathVariables, String uri) {
        if (pathVariables == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND, "URI에 path variables 없음", uri);
        }
        if (!pathVariables.containsKey("groupId")) {
            throw new NotFoundException(ErrorCode.NOT_FOUND, "URI에 그룹 id 포함되어 있지 않음", uri);
        }
        return String.valueOf(pathVariables.get("groupId"));
    }
}
