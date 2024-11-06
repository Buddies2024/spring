package com.exchangediary.notification.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.notification.domain.NotificationRepository;
import com.exchangediary.notification.domain.entity.Notification;
import com.exchangediary.notification.ui.dto.request.NotificationTokenRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationApiTest extends ApiBaseTest {
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void 알림_fcm_토큰_저장_성공() {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest("token"))
                .when().patch("/api/members/notifications/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Notification notification = notificationRepository.findByMemberId(member.getId()).get();
        assertThat(notification.getToken()).isEqualTo("token");
    }

    @Test
    void 알림_fcm_토큰_업데이트_성공() {
        RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest("old-token"))
                .when().patch("/api/members/notifications/token")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest("new-token"))
                .when().patch("/api/members/notifications/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Notification notification = notificationRepository.findByMemberId(member.getId()).get();
        assertThat(notification.getToken()).isEqualTo("new-token");
    }
}