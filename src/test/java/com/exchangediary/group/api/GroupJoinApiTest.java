package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GroupJoinApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/join";

    @Test
    @DisplayName("그룹 가입에 성공한다.")
    void Expect_JoinGroup() {
        // Given
        String nickname = "스프링";
        String profileImage = PROFILE_IMAGES[0];

        Group group = createGroup();
        joinGroup("리더", 1, GroupRole.GROUP_LEADER, group, createMember(1234L));

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupJoinRequest(profileImage, nickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);

        List<GroupMember> groupMembers = groupMemberRepository.findAll();
        assertThat(groupMembers).hasSize(2);

        GroupMember updatedMember = groupMembers.stream().filter(groupMember -> groupMember.getNickname().equals(nickname)).findFirst().get();
        assertThat(updatedMember.getNickname()).isEqualTo(nickname);
        assertThat(updatedMember.getProfileImage()).isEqualTo(profileImage);
        assertThat(updatedMember.getOrderInGroup()).isEqualTo(2);
        assertThat(updatedMember.getGroup().getId()).isEqualTo(group.getId());
        assertThat(updatedMember.getGroupRole()).isEqualTo(GroupRole.GROUP_MEMBER);
        assertThat(updatedMember.getLastViewableDiaryDate()).isEqualTo(group.getCreatedAt().toLocalDate().minusDays(1));
    }

    @Test
    @DisplayName("그룹의 정원이 가득차면, 409 예외를 반환한다.")
    void When_GroupHas7Member_Expect_Throw409Exception() {
        // Given
        String nickname = "스프링";
        String profileImage = PROFILE_IMAGES[0];

        Group group = createGroup();
        makeFullGroup(group);

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupJoinRequest(profileImage, nickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo(ErrorCode.FULL_MEMBERS_OF_GROUP.getMessage()));
    }

    @Test
    @DisplayName("프로필이 중복되면, 400 예외를 반환한다.")
    void When_ProfileImageIsDuplicated_Expect_Throw400Exception() {
        // Given
        String nickname = "스프링";
        String profileImage = PROFILE_IMAGES[0];

        Group group = createGroup();
        joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(1234L));

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupJoinRequest(profileImage, nickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ErrorCode.PROFILE_DUPLICATED.getMessage()));
    }

    @Test
    @DisplayName("닉네임이 중복되면, 400 예외를 반환한다.")
    void When_NicknameIsDuplicated_Expect_Throw400Exception() {
        // Given
        String nickname = "스프링";
        String profileImage = PROFILE_IMAGES[0];

        Group group = createGroup();
        joinGroup(nickname, 1, GroupRole.GROUP_LEADER, group, createMember(1234L));

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupJoinRequest(profileImage, nickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
