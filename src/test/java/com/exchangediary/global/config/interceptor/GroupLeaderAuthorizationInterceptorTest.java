package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.request.GroupLeaderHandOverRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class GroupLeaderAuthorizationInterceptorTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/leader/skip-order";

    @Test
    @DisplayName("사용자가 방장이면, 방장 권한을 행사할 수 있다.")
    void When_MemberIsReader_Expect_CanExerciseLeadersAuthority() {
        Group group = createGroup();
        joinGroup("리더", 0, 1, GroupRole.GROUP_LEADER, group, this.member);
        GroupMember others = joinGroup("그룹원", 1, 2, GroupRole.GROUP_MEMBER, group, createMember(1234L));

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(others.getNickname()))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자가 방장이 아니면, 403 예외를 반환한다.")
    void When_MemberIsNotReader_Expect_Throw403Exception() {
        Group group = createGroup();
        GroupMember others = joinGroup("리더", 0, 1, GroupRole.GROUP_LEADER, group, createMember(1234L));
        joinGroup("그룹원", 1, 2, GroupRole.GROUP_MEMBER, group, this.member);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(others.getNickname()))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
