package com.exchangediary.member.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.ui.dto.response.MemberNotificationResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class OnNotificationApiTest extends ApiBaseTest {
    private static final String URI = "/api/member/notification";

    @Test
    @DisplayName("사용자의 알림 활성화 여부를 조회한다.")
    void When_MembersOnNotificationIsTrue() {
        MemberNotificationResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(MemberNotificationResponse.class);

        assertThat(body.onNotification()).isTrue();
    }

    @Test
    @DisplayName("사용자의 알림 활성화를 끈다.")
    void When_MembersOnNotificationIsTrue_Expect_ChangeOnNotificationIsFalse() {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member updatedMember = memberRepository.findById(member.getId()).get();
        assertThat(updatedMember.getOnNotification()).isFalse();
    }
}
