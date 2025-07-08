package com.exchangediary.member.service;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CookieServiceTest {
    @Autowired
    private CookieService cookieService;

    @Test
    @DisplayName("키-값에 대응하는 쿠키를 생성한다.")
    void Expect_CreateCookie() {
        String name = "name";
        String value = "value";

        Cookie cookie = cookieService.createCookie(name, value);

        assertThat(cookie.getName()).isEqualTo(name);
        assertThat(cookie.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("키에 해당하는 쿠키 있으면 값을 반환한다.")
    void When_ExistKeyInCookies_Expect_GetValue() {
        Cookie[] cookies = {
                cookieService.createCookie("name1", "value1"),
                cookieService.createCookie("name2", "value2"),
                cookieService.createCookie("name3", "value3"),
        };

        String value = cookieService.getValueFromCookies(cookies, "name2");

        assertThat(value).isEqualTo("value2");
    }

    @Test
    @DisplayName("키에 해당하는 쿠키 없으면 예외를 던진다.")
    void When_NotExistKeyInCookies_Expect_ThrowException() {
        Cookie[] cookies = {
                cookieService.createCookie("name1", "value1"),
                cookieService.createCookie("name2", "value2"),
                cookieService.createCookie("name3", "value3"),
        };

        assertThrows(RuntimeException.class, () -> cookieService.getValueFromCookies(cookies, "empty"));
    }

    @Test
    @DisplayName("쿠키 리스트가 비어있으면 예외를 던진다.")
    void When_EmptyCookies_Expect_ThrowException() {
        assertThrows(RuntimeException.class, () -> cookieService.getValueFromCookies(null, "empty"));
    }
}
