package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GroupLeaveApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%d/leave";
    private static final String GROUP_NAME = "버니즈";

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("마지막 사람 나감, 그룹 현재 순서 나간 사람보다 전")
    public void 그룹_나가기_마지막_사람() {
        Group group = createGroup(1);
        groupRepository.save(group);
        Member groupMember = createMemberInGroup(group,1);
        Member groupMember2 = createMemberInGroup(group,2);
        member.joinGroup("api요청멤버", "orange", 3, GroupRole.GROUP_MEMBER, group);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when()
                .patch(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member updatedMember = memberRepository.findById(member.getId()).get();
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedMember.getGroup()).isEqualTo(null);
        assertThat(updatedMember.getNickname()).isEqualTo(null);
        assertThat(updatedMember.getGroupRole()).isEqualTo(null);
        assertThat(updatedMember.getProfileImage()).isEqualTo(null);
        assertThat(updatedMember.getOrderInGroup()).isEqualTo(0);
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(group.getCurrentOrder());
    }

    @Test
    @DisplayName("마지막 사람 나감, 그룹 현재 순서 나간 사람")
    public void 그룹_나가기_마지막_사람_현재순서변경() {
        Group group = createGroup(3);
        groupRepository.save(group);
        Member groupMember = createMemberInGroup(group,1);
        Member groupMember2 = createMemberInGroup(group,2);
        member.joinGroup("api요청멤버", "orange", 3, GroupRole.GROUP_MEMBER, group);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when()
                .patch(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member updatedMember = memberRepository.findById(member.getId()).get();
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedMember.getGroup()).isEqualTo(null);
        assertThat(updatedMember.getNickname()).isEqualTo(null);
        assertThat(updatedMember.getGroupRole()).isEqualTo(null);
        assertThat(updatedMember.getProfileImage()).isEqualTo(null);
        assertThat(updatedMember.getOrderInGroup()).isEqualTo(0);
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("중간 사람 나감, 그룹 현재 순서 나간 사람보다 전")
    public void 그룹_나가기_중간_사람() {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.joinGroup("api요청멤버", "orange", 1, GroupRole.GROUP_MEMBER, group);
        Member groupMember = createMemberInGroup(group,2);
        Member groupMember2 = createMemberInGroup(group,3);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when()
                .patch(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member updatedMember = memberRepository.findById(member.getId()).get();
        Member updatedGroupMember = memberRepository.findById(groupMember.getId()).get();
        Member updatedGroupMember2 = memberRepository.findById(groupMember2.getId()).get();
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedMember.getGroup()).isEqualTo(null);
        assertThat(updatedMember.getNickname()).isEqualTo(null);
        assertThat(updatedMember.getGroupRole()).isEqualTo(null);
        assertThat(updatedMember.getProfileImage()).isEqualTo(null);
        assertThat(updatedMember.getOrderInGroup()).isEqualTo(0);
        assertThat(updatedGroupMember.getOrderInGroup()).isEqualTo(1);
        assertThat(updatedGroupMember2.getOrderInGroup()).isEqualTo(2);
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(group.getCurrentOrder());
    }

    @Test
    @DisplayName("중간 사람 나감, 그룹 현재 순서 나간 사람보다 후")
    public void 그룹_나가기_중간_사람_현재순서변경() {
        Group group = createGroup(3);
        groupRepository.save(group);
        member.joinGroup("api요청멤버", "orange", 1, GroupRole.GROUP_MEMBER, group);
        Member groupMember = createMemberInGroup(group,2);
        Member groupMember2 = createMemberInGroup(group,3);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when()
                .patch(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member updatedMember = memberRepository.findById(member.getId()).get();
        Member updatedGroupMember = memberRepository.findById(groupMember.getId()).get();
        Member updatedGroupMember2 = memberRepository.findById(groupMember2.getId()).get();
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedMember.getGroup()).isEqualTo(null);
        assertThat(updatedMember.getNickname()).isEqualTo(null);
        assertThat(updatedMember.getGroupRole()).isEqualTo(null);
        assertThat(updatedMember.getProfileImage()).isEqualTo(null);
        assertThat(updatedMember.getOrderInGroup()).isEqualTo(0);
        assertThat(updatedGroupMember.getOrderInGroup()).isEqualTo(1);
        assertThat(updatedGroupMember2.getOrderInGroup()).isEqualTo(2);
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(2);
    }

    private Group createGroup(int currentOrder) {
        Group group = Group.builder()
                .name(GROUP_NAME)
                .currentOrder(currentOrder)
                .code("code")
                .lastSkipOrderDate(LocalDate.now())
                .build();
        groupRepository.save(group);
        return group;
    }

    private Member createMemberInGroup(Group group, int orderInGroup) {
        return Member.builder()
                .kakaoId(12345L)
                .orderInGroup(orderInGroup)
                .profileImage("red")
                .group(group)
                .build();
    }
}
