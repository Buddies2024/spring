package com.exchangediary.member.ui;

import com.exchangediary.member.service.CookieService;
import com.exchangediary.member.service.JwtService;
import com.exchangediary.member.service.KakaoService;
import com.exchangediary.member.service.MemberRegistrationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/kakao")
public class KakaoLoginController {
    private final KakaoService kakaoService;
    private final MemberRegistrationService memberRegistrationService;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @GetMapping("/callback")
    public String callback(
            @RequestParam String code,
            HttpServletResponse response
    ) {
        Long kakaoId = kakaoService.loginKakao(code);
        Long memberId = memberRegistrationService.getOrCreateMember(kakaoId).memberId();
        String token = jwtService.generateAccessToken(memberId);

        cookieService.addCookie(jwtService.COOKIE_NAME, token, response);
        return "redirect:/groups";
    }
}
