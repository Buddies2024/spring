package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.service.DiaryImageService;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class DiaryWriteApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries";
    private static final String TODAY_MOOD = "happy.png";

    @Autowired
    private DiaryRepository diaryRepository;
    @MockBean
    private DiaryImageService diaryImageService;

    /*
    일기 작성 시,
    1. 일기 내용(DiaryContent) 생성
    2. 그룹 순서(currentOrder) 변경
    3. 마지막 일기 조회 날짜(lastViewableDiaryDate) 변경
    */
    // TODO: 이미지 포함된 일기 작성을 위해 DiaryImageService 클래스 단위의 테스트 생성하기

    @Test
    @DisplayName("사진이 포함되지 않은 일기 작성에 성공한다.")
    void When_NotIncludeImage_Expect_SuccessWriteDiary() {
        // Given
        Group group = createGroup();
        joinGroup("나", 1, GroupRole.GROUP_MEMBER, group, member);
        GroupMember nextWriter = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(123L));

        List<String> diaryContents = List.of("오늘의", "날씨", "맑음", ":)");

        // When
        String location = RestAssured
                .given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("data", makeWriteDiaryRequestBody(TODAY_MOOD, diaryContents), "application/json")
                .cookie("token", token)
                .when().post(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .header("Content-Location");
        Long diaryId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));

        // Then
        Diary diary = diaryRepository.findById(diaryId).get();
        assertThat(diary.getImageFileName()).isNull();
        assertThat(diary.getTodayMood()).isEqualTo(TODAY_MOOD);

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(2);

        LocalDate currentWriterLastViewableDiaryDate = groupMemberRepository.findLastViewableDiaryDateByMemberId(member.getId()).get();
        assertThat(currentWriterLastViewableDiaryDate).isEqualTo(LocalDate.now());
        LocalDate nextWriterLastViewableDiaryDate = groupMemberRepository.findById(nextWriter.getId()).get().getLastViewableDiaryDate();
        assertThat(nextWriterLastViewableDiaryDate).isEqualTo(LocalDate.now());
    }

    private Map<String, Object> makeWriteDiaryRequestBody(String todayMood, List<String> contents) {
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("todayMood", todayMood);
        requestBody.put("contents", contents.stream()
                .map(content -> Map.of("content", content)));
        return requestBody;
    }
}
