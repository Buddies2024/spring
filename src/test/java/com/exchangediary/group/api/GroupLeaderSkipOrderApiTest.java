package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupLeaderSkipOrderApiTest extends ApiBaseTest {
    @Autowired
    private GroupRepository groupRepository;

    @Test
    void 일기_건너뛰기_성공() {
        Group group = createGroup(2);
        updateSelf(group, 1, GroupRole.GROUP_LEADER);
        createMember(group, 2, GroupRole.GROUP_MEMBER);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format("/api/groups/%s/leader/skip-order", group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(updatedGroup.getLastSkipOrderDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void 일기_건너뛰기_실패_오늘_이미_실행() {
        Group group = createGroup(2);
        updateSelf(group, 1, GroupRole.GROUP_LEADER);
        createMember(group, 2, GroupRole.GROUP_MEMBER);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format("/api/groups/%s/leader/skip-order", group.getId()))
                .then().log().all();
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format("/api/groups/%s/leader/skip-order", group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo(ErrorCode.ALREADY_SKIP_ORDER_TODAY.getMessage()));
        ;

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(updatedGroup.getLastSkipOrderDate()).isEqualTo(LocalDate.now());
    }

    private Group createGroup(int order) {
        Group group = Group.of("group-name", "code");
        group.updateCurrentOrder(order, 2);
        return groupRepository.save(group);
    }

    private void updateSelf(Group group, int order, GroupRole role) {
        this.member.joinGroup(
                "me",
                "red",
                order,
                role,
                group
        );
        memberRepository.save(this.member);
    }

    private Member createMember(Group group, int order, GroupRole role) {
        return memberRepository.save(Member.builder()
                .kakaoId(1L)
                .nickname("group-member")
                .profileImage("orange")
                .orderInGroup(order)
                .group(group)
                .groupRole(role)
                .build()
        );
    }
}
