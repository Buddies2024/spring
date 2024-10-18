package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.response.GroupMembersResponse;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupMembersOrderApiTest extends ApiBaseTest {
    private static final List<String> PROFILE_IMAGES = new ArrayList<>(List.of("red", "orange", "yellow", "green", "blue", "navy", "purple"));
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 그룹원_순서_조회_내순서_1번일때() {
        Group group = createGroup();
        groupRepository.save(group);
        this.member.updateMemberGroupInfo("self", PROFILE_IMAGES.get(0), 1, group);
        memberRepository.save(member);
        for (int idx = 6; idx > 0; idx--) {
            Member member = createMember(group, idx + 1, PROFILE_IMAGES.get(7 - idx));
            memberRepository.save(member);
        }

        var members = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when()
                .get("/api/groups/" + group.getId() + "/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupMembersResponse.class)
                .members();

        assertThat(members).hasSize(7);
        assertThat(members.get(0).isSelf()).isTrue();
        assertThat(members.get(0).nickname()).isEqualTo(this.member.getNickname());
        assertThat(members.get(1).nickname()).isEqualTo("name2");
        assertThat(members.get(2).nickname()).isEqualTo("name3");
        assertThat(members.get(3).nickname()).isEqualTo("name4");
        assertThat(members.get(4).nickname()).isEqualTo("name5");
        assertThat(members.get(5).nickname()).isEqualTo("name6");
        assertThat(members.get(6).nickname()).isEqualTo("name7");
        assertThat(members.get(1).isSelf()).isFalse();
        assertThat(members.get(2).isSelf()).isFalse();
        assertThat(members.get(3).isSelf()).isFalse();
        assertThat(members.get(4).isSelf()).isFalse();
        assertThat(members.get(5).isSelf()).isFalse();
        assertThat(members.get(6).isSelf()).isFalse();
    }

    @Test
    void 그룹원_순서_조회_내순서_4번일때() {
        Group group = createGroup();
        groupRepository.save(group);
        this.member.updateMemberGroupInfo("self", PROFILE_IMAGES.get(0), 4, group);
        memberRepository.save(member);
        for (int idx = 3; idx > 0; idx--) {
            Member member = createMember(group, idx, PROFILE_IMAGES.get(7 - idx));
            memberRepository.save(member);
        }
        for (int idx = 7; idx > 4; idx--) {
            Member member = createMember(group, idx, PROFILE_IMAGES.get(7 - idx));
            memberRepository.save(member);
        }

        var members = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when()
                .get("/api/groups/" + group.getId() + "/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupMembersResponse.class)
                .members();

        assertThat(members).hasSize(7);
        assertThat(members.get(3).isSelf()).isEqualTo(true);
        assertThat(members.get(0).nickname()).isEqualTo("name1");
        assertThat(members.get(1).nickname()).isEqualTo("name2");
        assertThat(members.get(2).nickname()).isEqualTo("name3");
        assertThat(members.get(3).nickname()).isEqualTo(this.member.getNickname());
        assertThat(members.get(4).nickname()).isEqualTo("name5");
        assertThat(members.get(5).nickname()).isEqualTo("name6");
        assertThat(members.get(6).nickname()).isEqualTo("name7");
    }

    @Test
    void 그룹원_순서_조회_내순서_7번일때() {
        Group group = createGroup();
        groupRepository.save(group);
        this.member.updateMemberGroupInfo("self", PROFILE_IMAGES.get(0), 7, group);
        memberRepository.save(member);
        for (int idx = 6; idx > 0; idx--) {
            Member member = createMember(group, idx, PROFILE_IMAGES.get(7 - idx));
            memberRepository.save(member);
        }

        var members = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when()
                .get("/api/groups/" + group.getId() + "/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(GroupMembersResponse.class)
                .members();

        assertThat(members).hasSize(7);
        assertThat(members.get(6).isSelf()).isEqualTo(true);
        assertThat(members.get(6).nickname()).isEqualTo(this.member.getNickname());
        assertThat(members.get(0).nickname()).isEqualTo("name1");
        assertThat(members.get(1).nickname()).isEqualTo("name2");
        assertThat(members.get(2).nickname()).isEqualTo("name3");
        assertThat(members.get(3).nickname()).isEqualTo("name4");
        assertThat(members.get(4).nickname()).isEqualTo("name5");
        assertThat(members.get(5).nickname()).isEqualTo("name6");
    }

    private Group createGroup() {
        return Group.builder()
                .name("groupname")
                .currentOrder(1)
                .code("code")
                .build();
    }

    private Member createMember(Group group, int index, String profileImage) {
        return Member.builder()
                .profileImage(profileImage)
                .kakaoId(1234L + index)
                .orderInGroup(index)
                .nickname("name" + index)
                .group(group)
                .build();
    }
}
