package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;

class GroupNicknameApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/nickname/verify";

    @Test
    @DisplayName("그룹에 중복되는 닉네임이 없으면, 유효성 검사에 성공한다.")
    void When_NoDuplicatedNicknameInGroup_Expect_Success() {
        // Given
        String nickname = "스프링";

        Group group = createGroup();
        joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(1234L));

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .queryParam("nickname", nickname)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("verification", equalTo(true));
    }

    @Test
    @DisplayName("그룹에 중복되는 닉네임이 있으면, 400 예외를 반환한다.")
    void When_NicknameDuplicateInGroup_Expect_Throw400Exception() {
        // Given
        String nickname = "스프링";

        Group group = createGroup();
        joinGroup(nickname, 0, GroupRole.GROUP_LEADER, group, createMember(1234L));

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .queryParam("nickname", nickname)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ErrorCode.NICKNAME_DUPLICATED.getMessage()));
    }
}
