package com.exchangediary.group.api;

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

import static org.assertj.core.api.Assertions.assertThat;

public class GroupLeaderHandOverApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/leader/hand-over";

    @Test
    @DisplayName("닉네임과 일치하는 그룹원이 있으면, 방장 권한을 넘긴다.")
    void When_NicknameExistInGroup_Expect_HandOverLeader() {
        // Given
        String nextLeaderNickname = "뉴방장";

        Group group = createGroup();
        GroupMember oldLeader = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, this.member);
        GroupMember newLeader = joinGroup(nextLeaderNickname, 1, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(nextLeaderNickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        GroupMember updatedNewLeader = groupMemberRepository.findById(newLeader.getId()).get();

        assertThat(updatedNewLeader.getGroupRole()).isEqualTo(GroupRole.GROUP_LEADER);

        GroupMember updatedOldLeader = groupMemberRepository.findById(oldLeader.getId()).get();

        assertThat(updatedOldLeader.getGroupRole()).isEqualTo(GroupRole.GROUP_MEMBER);
    }

    @Test
    @DisplayName("닉네임이 방장 닉네임과 일치하면, 아무일도 일어나지 않는다.")
    void When_NicknameMatchGroupLeader_Expect_DoNothing() {
        // Given
        String currentLeaderNickname = "스프링";

        Group group = createGroup();
        joinGroup(currentLeaderNickname, 0, GroupRole.GROUP_LEADER, group, this.member);
        joinGroup("그룹원", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(currentLeaderNickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        GroupMember leader = groupMemberRepository.findByMemberId(this.member.getId()).get();

        assertThat(leader.getGroupRole()).isEqualTo(GroupRole.GROUP_LEADER);
    }

    @Test
    @DisplayName("닉네임과 일치하는 그룹원이 없으면, 404 예외를 반환한다.")
    void When_NicknameIsNotFoundInGroup_Expect_Throw404Exception() {
        // Given
        String invalidNickname = "invalid-nickname";

        Group group = createGroup();
        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, this.member);
        joinGroup("그룹원", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(invalidNickname))
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
