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
    private static final String URI = "/api/members/notifications/token";
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void 알림_fcm_토큰_저장_성공() {
        String fcmToken = "token";

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest(fcmToken))
                .when().patch(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        List<Notification> notifications = notificationRepository.findAllByMemberId(member.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getToken()).isEqualTo(fcmToken);
    }

    @Test
    void 알림_fcm_토큰_업데이트_성공() {
        String fcmToken1 = "token1";
        String fcmToken2 = "token2";

        RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest(fcmToken1))
                .when().patch(URI)
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest(fcmToken2))
                .when().patch(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        List<Notification> notifications = notificationRepository.findAllByMemberId(member.getId());
        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0).getToken()).isEqualTo(fcmToken1);
        assertThat(notifications.get(1).getToken()).isEqualTo(fcmToken2);
    }
}
