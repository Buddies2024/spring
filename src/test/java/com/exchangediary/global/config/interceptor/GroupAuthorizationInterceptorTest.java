package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupAuthorizationInterceptorTest extends ApiBaseTest {
    private final String MONTHLY_URI = "/groups/%s";
    private final String CREATE_GROUP_URI = "/groups";
    private final String API_URI = "/api/groups/%s/members";

    @Test
    @DisplayName("사용자가 속한 그룹의 API 요청 시, 성공한다.")
    void When_RequestGroupApiWhichMemberBelong_Then_Success() {
        Group group = createGroup();
        joinGroup("스프링", 0, 1, GroupRole.GROUP_LEADER, group, this.member);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자가 속하지않은 그룹의 API 요청 시, 403 에러를 발생한다.")
    void When_RequestGroupApiWhichMemberNotBelong_Then_Throw403Exception() {
        Group group = createGroup();
        joinGroup("스프링", 0, 1, GroupRole.GROUP_LEADER, group, this.member);
        Group otherGroup = createGroup();

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, otherGroup.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("사용자가 어떤 그룹에도 속하지 않고 API 요청 시, 403 에러를 발생한다.")
    void When_RequestApiAndMemberNotBelongAnyGroup_Then_Throw403Exception() {
        Group group = createGroup();

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("사용자가 속한 그룹 페이지에 접근 시, 성공한다.")
    void When_RequestGroupPageWhichMemberBelong_Then_Success() {
        Group group = createGroup();
        joinGroup("스프링", 0, 1, GroupRole.GROUP_LEADER, group, this.member);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(MONTHLY_URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자가 속하지 않은 그룹 페이지에 접근 시, 403 페이지를 보여준다.")
    void When_RequestGroupPageWhichMemberNotBelong_Then_Redirect403Exception() {
        Group group = createGroup();
        joinGroup("스프링", 0, 1, GroupRole.GROUP_LEADER, group, this.member);
        Group otherGroup = createGroup();

        String contentType = RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format(MONTHLY_URI, otherGroup.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .extract()
                .header("Content-Type");

        assertThat(contentType.substring(0, contentType.indexOf(";"))).isEqualTo("text/html");
    }

    @Test
    @DisplayName("사용자가 그룹에 가입되어있고 그룹 생성 페이지에 접근 시, 시작 페이지로 리다이렉트한다.")
    void When_RequestGroupCreatePageWhichMemberBelong_Then_Success() {
        Group group = createGroup();
        joinGroup("스프링", 0, 1, GroupRole.GROUP_LEADER, group, this.member);

        String location = RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(CREATE_GROUP_URI)
                .then().log().all()
                .statusCode(HttpStatus.FOUND.value())
                .extract()
                .header("Location");

        assertThat(location.substring(location.lastIndexOf("/"))).isEqualTo("/");
    }

    @Test
    @DisplayName("사용자가 어떤 그룹에도 속하지 않고 그룹 생성 페이지에 접근 시, 성공한다.")
    void When_RequestGroupCreatePageWhichMemberNotBelong_Then_Success() {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(CREATE_GROUP_URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
