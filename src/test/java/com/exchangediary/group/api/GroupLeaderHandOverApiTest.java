package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupLeaderHandOverRequest;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupLeaderHandOverApiTest extends ApiBaseTest {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 그룹_리더_넘기기_성공() {
        Group group = createGroup();
        updateSelf(group, 1, GroupRole.GROUP_LEADER);
        Member member = createMember(group, 2, GroupRole.GROUP_MEMBER);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(member.getOrderInGroup() - 1))
                .when().patch(String.format("/api/groups/%s/leader/hand-over", group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member newLeader = memberRepository.findById(member.getId()).get();
        Member oldLeader = memberRepository.findById(this.member.getId()).get();
        assertThat(newLeader.getGroupRole()).isEqualTo(GroupRole.GROUP_LEADER);
        assertThat(oldLeader.getGroupRole()).isEqualTo(GroupRole.GROUP_MEMBER);
    }

    @Test
    void 그룹_리더_넘기기_실패_존재하지_않는_그룹원_인덱스() {
        Group group = createGroup();
        updateSelf(group, 1, GroupRole.GROUP_LEADER);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(new GroupLeaderHandOverRequest(1))
                .when().patch(String.format("/api/groups/%s/leader/hand-over", group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private Group createGroup() {
        return groupRepository.save(Group.of("group-name", "code"));
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
