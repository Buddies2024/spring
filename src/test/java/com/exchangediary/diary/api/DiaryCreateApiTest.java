package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DiaryCreateApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%d/diaries";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void 일기_작성_성공() throws JsonProcessingException {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, group);
        memberRepository.save(member);
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", jsonData, "application/json")
                        .multiPart("file", new File("src/test/resources/images/test.jpg"), "image/png")
                        .cookie("token", token)
                        .when().post(String.format(API_PATH, group.getId()))
                        .then().log().all()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .header("Content-Location")
                        .split("/")[4]
        );

        Diary newDiary = diaryRepository.findById(diaryId).get();
        assertThat(newDiary.getGroup().getId()).isEqualTo(group.getId());
        assertThat(newDiary.getMember().getId()).isEqualTo(member.getId());
        assertThat(newDiary.getContent()).isEqualTo(data.get("content"));
        assertThat(newDiary.getMoodLocation()).isEqualTo(data.get("moodLocation"));
    }

    @Test
    void 일기_작성_성공_내용만() throws JsonProcessingException {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, group);
        memberRepository.save(member);
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", jsonData, "application/json")
                        .cookie("token", token)
                        .when().post(String.format(API_PATH, group.getId()))
                        .then().log().all()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .header("Content-Location")
                        .split("/")[4]
        );

        Diary newDiary = diaryRepository.findById(diaryId).get();
        assertThat(newDiary.getGroup().getId()).isEqualTo(group.getId());
        assertThat(newDiary.getMember().getId()).isEqualTo(member.getId());
        assertThat(newDiary.getContent()).isEqualTo(data.get("content"));
        assertThat(newDiary.getMoodLocation()).isEqualTo(data.get("moodLocation"));
    }

    @Test
    void 일기_작성_실패_오늘작성완료() throws JsonProcessingException {
        Group group = createGroup(1);
        groupRepository.save(group);
        Diary diary = createDiary(group);
        diaryRepository.save(diary);
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        RestAssured
                .given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("data", jsonData, "application/json")
                .multiPart("file", new File("src/test/resources/images/test.jpg"), "image/png")
                .cookie("token", token)
                .when().post(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ErrorCode.DIARY_DUPLICATED.getMessage()));
    }

    @DisplayName("일기 작성시 그룹내 순서 갱신")
    @Test
    void 일기_작성_성공_순서_확인() throws JsonProcessingException {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, group);
        Member groupMember = createMemberInGroup(group);
        Member groupMember2 = createMemberInGroup(group);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", jsonData, "application/json")
                        .multiPart("file", new File("src/test/resources/images/test.jpg"), "image/png")
                        .cookie("token", token)
                        .when().post(String.format(API_PATH, group.getId()))
                        .then().log().all()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .header("Content-Location")
                        .split("/")[4]
        );

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        Diary newDiary = diaryRepository.findById(diaryId).get();
        assertThat(newDiary.getGroup().getId()).isEqualTo(group.getId());
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(2);
        assertThat(newDiary.getMember().getId()).isEqualTo(member.getId());
        assertThat(newDiary.getContent()).isEqualTo(data.get("content"));
        assertThat(newDiary.getMoodLocation()).isEqualTo(data.get("moodLocation"));
    }

    @DisplayName("일기 작성시 그룹내 순서 갱신 - 마지막 순서에서 첫번째 순서로 갱신")
    @Test
    void 일기_작성_성공_순서_확인_맨_첫_순서로() throws JsonProcessingException {
        Group group = createGroup(3);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 3, group);
        Member groupMember = createMemberInGroup(group);
        Member groupMember2 = createMemberInGroup(group);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", jsonData, "application/json")
                        .multiPart("file", new File("src/test/resources/images/test.jpg"), "image/png")
                        .cookie("token", token)
                        .when().post(String.format(API_PATH, group.getId()))
                        .then().log().all()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .header("Content-Location")
                        .split("/")[4]
        );

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        Diary newDiary = diaryRepository.findById(diaryId).get();
        assertThat(newDiary.getGroup().getId()).isEqualTo(group.getId());
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(newDiary.getMember().getId()).isEqualTo(member.getId());
        assertThat(newDiary.getContent()).isEqualTo(data.get("content"));
        assertThat(newDiary.getMoodLocation()).isEqualTo(data.get("moodLocation"));
    }

    @DisplayName("일기 작성시 그룹내 순서 갱신 - 내용만 있는 경우")
    @Test
    void 일기_작성_성공_순서_확인_내용만() throws JsonProcessingException {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, group);
        Member groupMember = createMemberInGroup(group);
        Member groupMember2 = createMemberInGroup(group);
        memberRepository.saveAll(Arrays.asList(member, groupMember, groupMember2));
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", jsonData, "application/json")
                        .cookie("token", token)
                        .when().post(String.format(API_PATH, group.getId()))
                        .then().log().all()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .header("Content-Location")
                        .split("/")[4]
        );

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        Diary newDiary = diaryRepository.findById(diaryId).get();
        assertThat(newDiary.getGroup().getId()).isEqualTo(group.getId());
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(2);
        assertThat(newDiary.getMember().getId()).isEqualTo(member.getId());
        assertThat(newDiary.getContent()).isEqualTo(data.get("content"));
        assertThat(newDiary.getMoodLocation()).isEqualTo(data.get("moodLocation"));
    }

    private Diary createDiary(Group group) {
        return Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .group(group)
                .build();
    }

    private Group createGroup(int currentOrder) {
        return Group.builder()
                .name("버니즈")
                .currentOrder(currentOrder)
                .code("code")
                .build();
    }

    private Member createMemberInGroup(Group group) {
        return Member.builder()
                .kakaoId(12345L)
                .profileImage("red")
                .group(group)
                .build();
    }
}
