package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.request.GroupKickOutRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupLeaderKickOutApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/leader/leave";

    @Test
    @DisplayName("그룹원 강퇴에 성공한다.")
    void Expect_SuccessGroupMemberKick() {
        // Given
        Group group = createGroup();
        joinGroup("스프링", 1, GroupRole.GROUP_LEADER, group, member);
        GroupMember groupMember = joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(new GroupKickOutRequest("그룹원"))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getMemberCount()).isEqualTo(1);

        boolean existsMember = groupMemberRepository.existsById(groupMember.getId());
        assertThat(existsMember).isFalse();
    }

    @Test
    @DisplayName("닉네임에 해당하는 그룹원이 없는 경우 400 예외를 발생한다.")
    void When_NonExistentMemberMappingWithNickname_Expect_Throw400Exception() {
        // Given
        Group group = createGroup();
        joinGroup("스프링", 1, GroupRole.GROUP_LEADER, group, member);
        joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(new GroupKickOutRequest("리더"))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo(ErrorCode.MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("방장은 강퇴시킬 수 없다.")
    void When_KickOutGroupLeader_Expect_Throw403Exception() {
        // Given
        Group group = createGroup();
        joinGroup("스프링", 1, GroupRole.GROUP_LEADER, group, member);
        joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(new GroupKickOutRequest("스프링"))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo(ErrorCode.GROUP_LEADER_LEAVE_FORBIDDEN.getMessage()));
    }
}
