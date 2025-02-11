package com.exchangediary.member.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.enums.GroupRole;
import com.exchangediary.member.ui.dto.response.AnonymousInfoResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AnonymousApiTest extends ApiBaseTest {
    private static final String URI = "/api/anonymous/info";
    @Autowired
    private GroupRepository groupRepository;

    @Test
    void 로그인한_그룹가입한_사용자() {
        Group group = createGroup();
        updateSelf(group);

        var body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isFalse();
        assertThat(body.groupId()).isEqualTo(group.getId());
    }

    @Test
    void 로그인_안한_그룹가입한_사용자() {
        Group group = createGroup();
        updateSelf(group);

        var body = RestAssured
                .given().log().all()
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isTrue();
        assertThat(body.groupId()).isNull();
    }

    @Test
    void 로그인한_그룹미가입_사용자() {
        var body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isFalse();
        assertThat(body.groupId()).isNull();
    }

    @Test
    void 로그인_안한_그룹미가입_사용자() {
        var body = RestAssured
                .given().log().all()
                .when().get(URI)
                .then().log().all()
                .extract().as(AnonymousInfoResponse.class);

        assertThat(body.shouldLogin()).isTrue();
        assertThat(body.groupId()).isNull();
    }

    private Group createGroup() {
        Group group = Group.from("버니즈");
        return groupRepository.save(group);
    }

    private void updateSelf(Group group) {
        member.joinGroup("닉넴", "red", 1, GroupRole.GROUP_LEADER, group);
        memberRepository.save(member);
    }
}
