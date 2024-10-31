package com.exchangediary.global.config.web.interceptor;

import com.exchangediary.diary.service.DiaryAuthorizationService;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class ViewDiaryAuthorizationInterceptor implements HandlerInterceptor {
    private final DiaryAuthorizationService diaryAuthorizationService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long diaryId = Long.valueOf(String.valueOf(pathVariables.get("diaryId")));
        Long memberId = (Long) request.getAttribute("memberId");

        try {
            return diaryAuthorizationService.canViewDiary(memberId, diaryId);
        } catch (ForbiddenException exception) {
            if (request.getRequestURI().contains("/api")) {
                throw exception;
            }
            Long groupId = (Long) request.getAttribute("groupId");
            response.sendRedirect("/group/" + groupId);
            return false;
        }
    }
}