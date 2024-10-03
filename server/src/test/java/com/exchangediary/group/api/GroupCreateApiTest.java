package com.exchangediary.group.api;

import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.ui.dto.request.GroupNameRequest;
import com.exchangediary.group.ui.dto.response.GroupIdResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupCreateApiTest {
    private static final String API_PATH = "/api/groups";
    @LocalServerPort
    private int port;
    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        groupRepository.deleteAllInBatch();
    }

    @Test
    void 그룹_생성_성공() {
        String groupName = "버니즈";

        Long groupId = RestAssured
                .given().log().all()
                .body(new GroupNameRequest(groupName))
                .contentType(ContentType.JSON)
                .when().post(API_PATH)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(GroupIdResponse.class)
                .groupId();

        // ToDo: 아래 부분 location으로 확인하도록 수정
        String result = groupRepository.findById(groupId).get().getName();
        assertThat(result).isEqualTo(groupName);
    }
}