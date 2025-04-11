package com.exchangediary.member.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.ui.dto.response.AnonymousInfoResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnonymousApiTest extends ApiBaseTest {
    private static final String URI = "/api/anonymous/info";

    @Test
    @DisplayName("로그인 O, 그룹 가입 O 사용자")
    void When_LoginAndBelongToGroup() {
        Group group = createGroup();
        joinGroup("스프링", 2, GroupRole.GROUP_LEADER, group, member);

        AnonymousInfoResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isFalse();
        assertThat(body.groupId()).isEqualTo(group.getId());
    }

    @Test
    @DisplayName("로그인 X, 그룹 가입 O 사용자")
    void When_NoLoginAndBelongToGroup() {
        Group group = createGroup();
        joinGroup("스프링", 2, GroupRole.GROUP_LEADER, group, member);

        AnonymousInfoResponse body = RestAssured
                .given().log().all()
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isTrue();
        assertThat(body.groupId()).isNull();
    }

    @Test
    @DisplayName("로그인 O, 그룹 가입 X 사용자")
    void When_LoginAndNotBelongToGroup() {
        AnonymousInfoResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isFalse();
        assertThat(body.groupId()).isNull();
    }

    @Test
    @DisplayName("로그인 X, 그룹 가입 X 사용자")
    void When_NoLoginAndNotBelongToGroup() {
        AnonymousInfoResponse body = RestAssured
                .given().log().all()
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isTrue();
        assertThat(body.groupId()).isNull();
    }
}
