package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class GroupAuthorizationInterceptorTest extends ApiBaseTest {
    private final String URI = "/api/groups/%s/members";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MemberRepository memberRepository;
    private String groupId;

    @BeforeEach
    void joinGroup() {
        Group group = createGroup();
        updateSelf(group);
        groupId = group.getId();
    }

    @Test
    void 본인이_가입한_그룹_API_요청() {
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 본인이_가입하지않은_그룹_API_요청 () {
        Group group = createGroup();

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void 그룹_가입하지않은_사용자의_API_요청 () {
        Group group = createGroup();
        updateSelf(group);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format(URI, groupId))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private Group createGroup() {
        return groupRepository.save(Group.from("group-name"));
    }

    private void updateSelf(Group group) {
        this.member.joinGroup(
                "me",
                "red",
                1,
                GroupRole.GROUP_LEADER,
                group
        );
        memberRepository.save(this.member);
    }
}
