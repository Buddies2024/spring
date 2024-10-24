package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.ui.dto.request.GroupKickOutRequest;
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

public class GroupLeaderKickOutApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%d/leader/leave";
    private static final String GROUP_NAME = "버니즈";
    @Autowired
    GroupRepository groupRepository;

    @Test
    @DisplayName("중간 사람 강퇴, 그룹 현재 순서 나간 사람보다 후")
    public void 그룹_강퇴() {
        Group group = createGroup(3);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, GroupRole.GROUP_LEADER, group);
        Member kickOutMember = createMemberInGroup(group,2, "하니");
        Member groupMember = createMemberInGroup(group,3, "민지");
        memberRepository.saveAll(Arrays.asList(member, kickOutMember, groupMember));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(new GroupKickOutRequest("하니"))
                .when()
                .patch(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member updatedKickOutMember = memberRepository.findById(kickOutMember.getId()).get();
        Member updatedGroupMember = memberRepository.findById(groupMember.getId()).get();
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedKickOutMember.getGroup()).isEqualTo(null);
        assertThat(updatedKickOutMember.getNickname()).isEqualTo(null);
        assertThat(updatedKickOutMember.getGroupRole()).isEqualTo(null);
        assertThat(updatedKickOutMember.getProfileImage()).isEqualTo(null);
        assertThat(updatedKickOutMember.getOrderInGroup()).isEqualTo(0);
        assertThat(updatedGroupMember.getOrderInGroup()).isEqualTo(2);
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

    private Member createMemberInGroup(Group group, int orderInGroup, String nickname) {
        return Member.builder()
                .kakaoId(12345L)
                .nickname(nickname)
                .orderInGroup(orderInGroup)
                .profileImage("red")
                .group(group)
                .build();
    }
}
