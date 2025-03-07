package com.exchangediary.global.config.web;

import com.exchangediary.global.config.web.interceptor.GroupLeaderAuthorizationInterceptor;
import com.exchangediary.global.config.web.interceptor.GroupAuthorizationInterceptor;
import com.exchangediary.global.config.web.interceptor.JwtAuthenticationInterceptor;
import com.exchangediary.group.service.GroupLeaderService;
import com.exchangediary.member.service.CookieService;
import com.exchangediary.member.service.JwtService;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final MemberQueryService memberQueryService;
    private final GroupLeaderService groupLeaderService;

    @Value("${file.resources.location}")
    private String location;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/upload/**")
                .addResourceLocations("file://" + location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthenticationInterceptor(jwtService, cookieService, memberQueryService))
                .addPathPatterns("/groups", "/diaries/**", "/groups/**", "/api/**")
                .excludePathPatterns("/", "/login", "/api/kakao/callback", "/api/anonymous/info");
        registry.addInterceptor(new GroupAuthorizationInterceptor(memberQueryService))
                .addPathPatterns("/api/groups/*/**")
                .excludePathPatterns(
                        "/api/groups/*/profile-image",
                        "/api/groups/*/nickname/verify",
                        "/api/groups/*/join",
                        "/api/groups/code/verify"
                );
        registry.addInterceptor(new GroupLeaderAuthorizationInterceptor(groupLeaderService))
                .addPathPatterns("/api/groups/*/leader/**");
    }
}
