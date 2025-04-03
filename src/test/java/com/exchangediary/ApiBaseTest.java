package com.exchangediary;

import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.JwtService;
import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ApiBaseTest {
    public static final String GROUP_NAME = "버디즈";
    public static final String[] PROFILE_IMAGES = {"red", "orange", "yellow", "green", "blue", "navy", "purple"};

    @LocalServerPort
    private int port;
    @Autowired
    protected JwtService jwtService;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected GroupRepository groupRepository;
    @Autowired
    protected GroupMemberRepository groupMemberRepository;

    protected final Long kakaoId = 1L;
    protected Member member;
    protected String token;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        this.member = createMember(kakaoId);
        this.token = getToken();
    }

    protected Member createMember(Long kakaoId) {
        Member member = Member.from(kakaoId);
        return memberRepository.save(member);
    }

    private String getToken() {
        return jwtService.generateAccessToken(member.getId());
    }

    protected Group createGroup() {
        Group group = Group.from(GROUP_NAME);
        return groupRepository.save(group);
    }

    protected GroupMember joinGroup(String nickname, int profileImageIndex, int orderInGroup, GroupRole groupRole, Group group, Member member) {
        group.joinMember();
        groupRepository.save(group);
        GroupMember groupMember = GroupMember.of(
                nickname,
                PROFILE_IMAGES[profileImageIndex],
                orderInGroup,
                groupRole,
                group,
                member
        );
        return groupMemberRepository.save(groupMember);
    }
}
