package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.UploadImageRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.UploadImage;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DiaryWriteApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%d/diaries";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UploadImageRepository uploadImageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 일기_작성_성공_사진포함() throws JsonProcessingException {
        Group group = createGroup(1);
        updateSelf(group, 1);
        Map<String, String> data = makeDiaryData();

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
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
        UploadImage uploadImage = uploadImageRepository.findAll().getLast();
        assertThat(uploadImage.getDiary().getId()).isEqualTo(newDiary.getId());
    }

    @Test
    void 일기_작성_성공_사진_미포함() throws JsonProcessingException {
        Group group = createGroup(1);
        updateSelf(group, 1);
        Map<String, String> data = makeDiaryData();

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
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
        createDiary(group);
        Map<String, String> data = makeDiaryData();

        RestAssured
                .given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
                .multiPart("file", new File("src/test/resources/images/test.jpg"), "image/png")
                .cookie("token", token)
                .when().post(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ErrorCode.DIARY_DUPLICATED.getMessage()));
    }

    @Test
    @DisplayName("일기 작성시 그룹내 순서 갱신")
    void 일기_작성_성공_순서_확인() throws JsonProcessingException {
        Group group = createGroup(1);
        updateSelf(group, 1);
        createMember(group, 2);
        createMember(group, 3);
        Map<String, String> data = makeDiaryData();

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
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

    @Test
    @DisplayName("일기 작성시 그룹내 순서 갱신 - 마지막 순서에서 첫번째 순서로 갱신")
    void 일기_작성_성공_순서_확인_맨_첫_순서로() throws JsonProcessingException {
        Group group = createGroup(3);
        updateSelf(group, 3);
        createMember(group, 1);
        createMember(group, 2);
        Map<String, String> data = makeDiaryData();

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
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

    @Test
    @DisplayName("일기 작성시 그룹내 순서 갱신 - 내용만 있는 경우")
    void 일기_작성_성공_순서_확인_내용만() throws JsonProcessingException {
        Group group = createGroup(1);
        updateSelf(group, 1);
        createMember(group, 2);
        createMember(group, 3);
        Map<String, String> data = makeDiaryData();

        Long diaryId = Long.parseLong(
                RestAssured
                        .given().log().all()
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
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

    @Test
    void 일기_작성_실패_이미지_형식_실패() throws JsonProcessingException {
        Group group = createGroup(1);
        updateSelf(group, 1);
        Map<String, String> data = makeDiaryData();

        RestAssured
                .given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
                .multiPart("file", new File("src/main/resources/static/images/character/red.svg"), "image/svg")
                .cookie("token", token)
                .when().post(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body("message", equalTo(ErrorCode.INVALID_IMAGE_FORMAT.getMessage()));
    }

    @Test
    void 일기_작성_성공시_조회가능한_마지막_일기_날짜_업데이트_확인() throws JsonProcessingException {
        Group group = createGroup(1);
        updateSelf(group, 1);
        member.updateLastViewableDiaryDate(LocalDate.now().minusMonths(1));
        Member nextMember = createMember(group, 2);
        Map<String, String> data = makeDiaryData();

        RestAssured
                .given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("data", objectMapper.writeValueAsString(data), "application/json")
                .cookie("token", token)
                .when().post(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        Member writer = memberRepository.findById(this.member.getId()).get();
        Member nextWriter = memberRepository.findById(nextMember.getId()).get();
        assertThat(writer.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());
        assertThat(nextWriter.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());
    }

    private Diary createDiary(Group group) {
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .group(group)
                .build();
        return diaryRepository.save(diary);
    }

    private Group createGroup(int currentOrder) {
        Group group = Group.builder()
                .name("버니즈")
                .currentOrder(currentOrder)
                .code("code")
                .lastSkipOrderDate(LocalDate.now())
                .build();
        return groupRepository.save(group);
    }

    private void updateSelf(Group group, int order) {
        member.joinGroup("api요청멤버", "orange", order, GroupRole.GROUP_MEMBER, group);
        memberRepository.save(member);
    }

    private Member createMember(Group group, int order) {
        Member member = Member.builder()
                .kakaoId(12345L)
                .profileImage("red")
                .lastViewableDiaryDate(LocalDate.now().minusDays(1))
                .orderInGroup(order)
                .group(group)
                .build();
        return memberRepository.save(member);
    }

    private Map<String, String> makeDiaryData() {
        Map<String, String> data = new HashMap<>();
        data.put("content", "buddies");
        data.put("moodLocation", "/images/sad.png");
        return data;
    }
}
