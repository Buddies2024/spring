package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.domain.entity.Member;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupLeaderSkipOrderApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/leader/skip-order";

    @Test
    @DisplayName("현재 일기 작성 순서자가 2번이면, 일기 건너뛰기 권한 실행 시 3번째 사람이 일기 작성자가 된다.")
    void When_CurrentWriterOrderIs2_Expect_NextWriterOrderIs3() {
        // Given
        Group group = createGroup();

        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, this.member);
        joinGroup("현재작성자", 1, GroupRole.GROUP_MEMBER, LocalDate.now().minusDays(1), group, createMember(2L));
        GroupMember nextWriter = joinGroup("다음작성자", 2, GroupRole.GROUP_MEMBER, LocalDate.now().minusDays(2), group, createMember(3L));

        group.changeCurrentOrder(2);
        groupRepository.save(group);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        Group updatedGroup = groupRepository.findById(group.getId()).get();

        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(3);
        assertThat(updatedGroup.getLastSkipOrderDate()).isEqualTo(LocalDate.now());

        GroupMember updatedNextWriter = groupMemberRepository.findById(nextWriter.getId()).get();

        assertThat(updatedNextWriter.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("현재 일기 작성 순서자가 그룹 내 마지막 순서이면, 일기 건너뛰기 권한 실행 시 첫번째 사람이 일기 작성자가 된다.")
    void When_CurrentWriterOrderIsLast_Expect_NextWriterOrderIs1() {
        // Given
        Group group = createGroup();

        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, LocalDate.now().minusDays(2), group, this.member);
        joinGroup("그룹원", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        joinGroup("현재작성자", 2, GroupRole.GROUP_MEMBER, LocalDate.now().minusDays(1), group, createMember(3L));

        group.changeCurrentOrder(3);
        groupRepository.save(group);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        Group updatedGroup = groupRepository.findById(group.getId()).get();

        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(updatedGroup.getLastSkipOrderDate()).isEqualTo(LocalDate.now());

        GroupMember updatedNextWriter = groupMemberRepository.findByMemberId(this.member.getId()).get();

        assertThat(updatedNextWriter.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());
    }


    @Test
    @DisplayName("그룹원이 한 명이면, 일기 건너뛰기 권한 실행 시 또 내가 일기 작성자가 된다.")
    void When_GroupHasOnly1Member_Expect_NextWriterIsMe() {
        // Given
        Group group = createGroup();

        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, LocalDate.now().minusDays(2), group, this.member);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        Group updatedGroup = groupRepository.findById(group.getId()).get();

        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(updatedGroup.getLastSkipOrderDate()).isEqualTo(LocalDate.now());

        GroupMember updatedNextWriter = groupMemberRepository.findByMemberId(this.member.getId()).get();

        assertThat(updatedNextWriter.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("일기 건너뛰기 권한은 하루에 한 번만 실행 가능하다.")
    void When_AlreadyDoSkipOrder_Expect_Throw409Exception() {
        // Given
        Group group = createGroup();

        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, this.member);
        joinGroup("그룹원", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        joinGroup("현재작성자", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));

        RestAssured
                .given()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()));

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo(ErrorCode.ALREADY_SKIP_ORDER_TODAY.getMessage()));
    }

    private GroupMember joinGroup(String nickname, int profileImageIndex, GroupRole groupRole, LocalDate lastViewableDiaryDate, Group group, Member member) {
        group.joinMember();
        groupRepository.save(group);
        GroupMember groupMember = GroupMember.builder()
                .nickname(nickname)
                .profileImage(PROFILE_IMAGES[profileImageIndex])
                .orderInGroup(group.getMemberCount())
                .groupRole(groupRole)
                .lastViewableDiaryDate(lastViewableDiaryDate)
                .group(group)
                .member(member)
                .build();
        return groupMemberRepository.save(groupMember);
    }
}
