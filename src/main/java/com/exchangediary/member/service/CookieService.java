package com.exchangediary.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CookieService {
    @Value("${cookie.max-age.millisecond}")
    private int maxAgeInMilliseconds;

    public void addCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = createCookie(name, value);
        response.addCookie(cookie);
    }

    public Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInMilliseconds / 1000);
        return cookie;
    }

    public String getValueFromCookies(Cookie[] cookies, String name) throws RuntimeException {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getValue();
    }
}
