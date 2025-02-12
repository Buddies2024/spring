package com.exchangediary;

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
    @LocalServerPort
    private int port;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    private JwtService jwtService;

    protected Member member;
    protected String token;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        init();
    }

    protected void init() {
        this.member = createMember();
        memberRepository.save(this.member);
        this.token = getToken();
    }

    private Member createMember() {
        return Member.builder()
                .kakaoId(1L)
                .orderInGroup(0)
                .build();
    }

    private String getToken() {
        return jwtService.generateAccessToken(member.getId());
    }
}
