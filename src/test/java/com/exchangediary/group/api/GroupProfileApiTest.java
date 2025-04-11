package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.response.GroupProfileResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GroupProfileApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/profile-image";

    @Test
    @DisplayName("이미 선택된 프로필 이미지 리스트 가져오기")
    void Expect_GetSelectedProfileImages() {
        // Given
        Group group = createGroup();

        joinGroup("레드", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        joinGroup("오렌지", 1, GroupRole.GROUP_MEMBER, group, createMember(3L));

        // When
        GroupProfileResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupProfileResponse.class);

        // Then
        assertThat(response.selectedImages()).hasSize(2);
    }

    @Test
    @DisplayName("그룹에 가입된 멤버 없는 경우, 빈 리스트를 반환한다.")
    void When_GroupHasNoMember_Expect_GetEmptyList() {
        Group group = createGroup();

        GroupProfileResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupProfileResponse.class);

        assertThat(response.selectedImages()).hasSize(0);
    }

    @Test
    @DisplayName("그룹이 가득 찬 경우, 선택할 수 있는 프로필 이미지가 없다.")
    void When_GroupHasNoMember_Expect_GetListWithSize7() {
        Group group = createGroup();
        makeFullGroup(group);

        GroupProfileResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupProfileResponse.class);

        assertThat(response.selectedImages()).hasSize(7);
    }

    @Test
    @DisplayName("존재하지 않는 그룹인 경우, 404 예외를 반환한다.")
    void When_GroupNotFound_Expect_Throw404Exception() {
        String groupId = "qwer1234";

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
