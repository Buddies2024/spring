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

import java.util.List;

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

        List<Notification> notifications = notificationRepository.findByMemberId(member.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getToken()).isEqualTo("token");
    }

    @Test
    void 동일한_fcm_토큰_저장() {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest("token"))
                .when().patch("/api/members/notifications/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest("token"))
                .when().patch("/api/members/notifications/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        List<Notification> notifications = notificationRepository.findByMemberId(member.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getToken()).isEqualTo("token");
    }

    @Test
    void 알림_fcm_토큰_여러개_저장() {
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

        List<Notification> notifications = notificationRepository.findByMemberId(member.getId());
        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0).getToken()).isEqualTo("old-token");
        assertThat(notifications.get(1).getToken()).isEqualTo("new-token");
    }
}
