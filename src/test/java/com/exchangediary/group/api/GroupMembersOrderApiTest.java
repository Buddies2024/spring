package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.ui.dto.response.GroupMembersResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupMembersOrderApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/members";

    @Test
    @DisplayName("사용자는 3번이고, 리더는 1번, 현재 일기 작성자는 5번인 경우")
    void Expect_SelfIs2AndLeaderIs0AndWriterIs4() {
        // Given
        Group group = createGroup();

        joinGroup("리더", 1, 1, GroupRole.GROUP_LEADER, group, createMember(1234L));
        for (int idx = 1 ; idx < 7; idx++) {
            if (idx == 2) {
                joinGroup("스프링", idx, idx + 1, GroupRole.GROUP_MEMBER, group, this.member);
            } else {
                joinGroup("그룹원" + idx, idx, idx + 1, GroupRole.GROUP_MEMBER, group, createMember(idx * 10L));
            }
        }
        group.changeCurrentOrder(5);
        groupRepository.save(group);

        // When
        GroupMembersResponse response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupMembersResponse.class);

        // Then
        List<GroupMembersResponse.GroupMemberResponse> members = response.members();

        assertThat(members).hasSize(7);

        assertThat(members.get(0).nickname()).isEqualTo("리더");
        assertThat(members.get(1).nickname()).isEqualTo("그룹원1");
        assertThat(members.get(2).nickname()).isEqualTo("스프링");
        assertThat(response.selfIndex()).isEqualTo(2);
        assertThat(response.leaderIndex()).isEqualTo(0);
        assertThat(response.currentWriterIndex()).isEqualTo(4);
    }

    @Test
    @DisplayName("그룹원이 한 명인 경우")
    void When_MemberIsSolo_Expect_AllIndexIs0() {
        Group group = createGroup();

        joinGroup("스프링", 1, 1, GroupRole.GROUP_LEADER, group, this.member);

        GroupMembersResponse response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupMembersResponse.class);

        List<GroupMembersResponse.GroupMemberResponse> members = response.members();

        assertThat(members).hasSize(1);

        assertThat(members.get(0).nickname()).isEqualTo("스프링");
        assertThat(response.selfIndex()).isEqualTo(0);
        assertThat(response.leaderIndex()).isEqualTo(0);
        assertThat(response.currentWriterIndex()).isEqualTo(0);
    }
}
