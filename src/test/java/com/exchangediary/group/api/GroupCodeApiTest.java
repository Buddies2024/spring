package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupCodeRequest;
import com.exchangediary.group.ui.dto.response.GroupIdResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupCodeApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/code/verify";

    @Test
    @DisplayName("존재하는 그룹의 그룹코드인 경우, 검증에 성공한다.")
    void When_GroupCodeIsValid_Then_SuccessValidation() {
        Group group = createGroup();

        GroupIdResponse response = RestAssured
                .given().log().all()
                .body(new GroupCodeRequest(group.getId()))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(URI)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupIdResponse.class);

        assertThat(response.groupId()).isEqualTo(group.getId());
    }

    @Test
    @DisplayName("존재하지 않는 그룹의 그룹코드인 경우, 404 예외를 반환한다.")
    void When_GroupCodeIsInValid_Then_Throw404Exception() {
        RestAssured
                .given().log().all()
                .body(new GroupCodeRequest("invalid-code"))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(URI)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("공백의 그룹코드인 경우, 400 예외를 반환한다.")
    void When_GroupCodeIsEmpty_Then_Throw400Exception(String code) {
        RestAssured
                .given().log().all()
                .body(new GroupCodeRequest(code))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(URI)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
