package com.exchangediary.notification.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.notification.domain.NotificationRepository;
import com.exchangediary.notification.ui.dto.request.NotificationTokenRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("새로운 fcm 토큰 저장 요청 시, notification 객체가 생성된다.")
    void When_requestNewFcmToken_Expect_createNotification() {
        String fcmToken = "token";

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest(fcmToken))
                .when().patch(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        List<String> notifications = notificationRepository.findTokensByMemberId(member.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0)).isEqualTo(fcmToken);
    }

    @Test
    @DisplayName("이미 저장되어있는 fcm 토큰 저장 요청 시, 아무 일도 일어나지 않는다.")
    void When_requestSameFcmToken_Expect_notCreateNewNotification() {
        String fcmToken = "token";

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest(fcmToken))
                .when().patch(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new NotificationTokenRequest(fcmToken))
                .when().patch(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        List<String> notifications = notificationRepository.findTokensByMemberId(member.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0)).isEqualTo("token");
    }

    @Test
    @DisplayName("한 사용자가 여러 개의 fcm 토큰을 저장할 수 있다.")
    void When_requestToSaveTwoFcmTokenForOneMember_Expect_createTwoNotifications() {
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


        List<String> notifications = notificationRepository.findTokensByMemberId(member.getId());
        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0)).isEqualTo(fcmToken1);
        assertThat(notifications.get(1)).isEqualTo(fcmToken2);
    }
}
