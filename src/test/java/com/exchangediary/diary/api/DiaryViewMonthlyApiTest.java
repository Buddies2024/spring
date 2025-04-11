package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.diary.ui.dto.response.DiaryDayResponse;
import com.exchangediary.diary.ui.dto.response.DiaryMonthlyResponse;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DiaryViewMonthlyApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries/monthly";
    private static final String TODAY_MOOD = "happy.png";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;

    @Test
    @DisplayName("사용자가 모든 일기 조회 가능한 경우")
    void When_CanViewAllDiary() {
        Group group = createGroup();
        GroupMember groupMember = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        Diary diary = writeDiary(groupMember, group, List.of("오늘 일기"));
        groupMember.updateLastViewableDiaryDate();
        groupMemberRepository.save(groupMember);

        DiaryMonthlyResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .queryParam("year", LocalDate.now().getYear())
                .queryParam("month", LocalDate.now().getMonthValue())
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryMonthlyResponse.class);

        assertThat(body.days()).hasSize(1);
        DiaryDayResponse day = body.days().get(0);
        assertThat(day.canView()).isTrue();
        assertThat(day.id()).isEqualTo(diary.getId());
        assertThat(day.day()).isEqualTo(diary.getCreatedAt().getDayOfMonth());
        assertThat(day.profileImage()).isEqualTo(groupMember.getProfileImage());
    }

    @Test
    @DisplayName("사용자가 어제 일기까지 조회 가능한 경우")
    void When_CanViewEvenYesterdayDiary() {
        Group group = createGroup();
        GroupMember groupMember = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        Diary diary = writeDiary(groupMember, group, List.of("오늘 일기"));

        DiaryMonthlyResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .queryParam("year", LocalDate.now().getYear())
                .queryParam("month", LocalDate.now().getMonthValue())
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryMonthlyResponse.class);

        assertThat(body.days()).hasSize(1);
        DiaryDayResponse day = body.days().get(0);
        assertThat(day.canView()).isFalse();
        assertThat(day.id()).isEqualTo(diary.getId());
        assertThat(day.day()).isEqualTo(diary.getCreatedAt().getDayOfMonth());
        assertThat(day.profileImage()).isEqualTo(groupMember.getProfileImage());
    }

    @Test
    @DisplayName("해당 년월에 작성된 일기가 없으면, 빈 리스트를 반환한다.")
    void When_NoDiaryWrittenInYearAndMonth_Expect_ReturnEmptyList() {
        Group group = createGroup();
        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);

        DiaryMonthlyResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .queryParam("year", LocalDate.now().getYear())
                .queryParam("month", LocalDate.now().getMonthValue())
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryMonthlyResponse.class);

        assertThat(body.days()).hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 13})
    @DisplayName("월이 유효하지 않은 경우, 400 예외를 발생한다.")
    void When_InvalidYearAndMonth_Expect_Throw400Exception(int month) {
        Group group = createGroup();
        GroupMember groupMember = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        writeDiary(groupMember, group, List.of("오늘 일기"));

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .queryParam("year", LocalDate.now().getYear())
                .queryParam("month", month)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ErrorCode.INVALID_DATE.getMessage()));
    }

    private Diary writeDiary(GroupMember writer, Group group, List<String> contents) {
        Diary diary = Diary.of(TODAY_MOOD, writer, group);
        diaryRepository.save(diary);

        for (int idx = 0; idx < contents.size(); idx++) {
            DiaryContent diaryContent = DiaryContent.of(idx + 1, contents.get(idx), diary);
            diaryContentRepository.save(diaryContent);
        }
        return diary;
    }
}
