package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.request.GroupCreateRequest;
import com.exchangediary.group.ui.dto.response.GroupCreateResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupCreateApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups";

    @Test
    @DisplayName("그룹 생성에 성공한다.")
    void Expect_CreateGroup() {
        String groupName = GROUP_NAME;
        String profileImage = PROFILE_IMAGES[0];
        String nickname = "스프링";

        var response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupCreateRequest(groupName, profileImage, nickname))
                .when().post(URI)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(GroupCreateResponse.class);

        // Then
        Group group = groupRepository.findById(response.groupId()).get();
        assertThat(group.getName()).isEqualTo(groupName);
        assertThat(group.getCurrentOrder()).isEqualTo(1);
        assertThat(group.getMemberCount()).isEqualTo(1);
        assertThat(group.getLastSkipOrderDate()).isEqualTo(LocalDate.now().minusDays(1));

        GroupMember groupMember = groupMemberRepository.findByMemberId(member.getId()).get();

        assertThat(groupMember.getGroup().getId()).isEqualTo(group.getId());
        assertThat(groupMember.getOrderInGroup()).isEqualTo(1);
        assertThat(groupMember.getNickname()).isEqualTo(nickname);
        assertThat(groupMember.getProfileImage()).isEqualTo(profileImage);
        assertThat(groupMember.getGroupRole()).isEqualTo(GroupRole.GROUP_LEADER);
        assertThat(groupMember.getLastViewableDiaryDate()).isEqualTo(group.getCreatedAt().toLocalDate().minusDays(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    @DisplayName("그룹 생성자의 닉네임이 공백인 경우 그룹 생성에 실패한다.")
    void When_GroupCreatorNicknameIsEmpty_Expect_CreateGroup(String nickname) {
        String groupName = GROUP_NAME;
        String profileImage = PROFILE_IMAGES[0];

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupCreateRequest(groupName, profileImage, nickname))
                .when().post(URI)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    @DisplayName("그룹 생성자의 프로필 이미지가 공백인 경우 그룹 생성에 실패한다.")
    void When_GroupCreatorProfileImageIsEmpty_Expect_CreateGroup(String profileImage) {
        String groupName = GROUP_NAME;
        String nickname = "스프링";

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupCreateRequest(groupName, profileImage, nickname))
                .when().post(URI)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
