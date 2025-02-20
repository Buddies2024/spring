package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.ui.dto.response.DiaryDayResponse;
import com.exchangediary.diary.ui.dto.response.DiaryMonthlyResponse;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DiaryViewMonthlyApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%s/diaries/monthly";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    @DisplayName("사용자가 오늘 일기까지 조회 가능")
    void 달력형_일기_조회_성공_조회_가능한_일기() {
        Group group = createGroup();
        updateSelf(group, true);
        Diary diary = createDiary(group);

        DiaryMonthlyResponse response = RestAssured
                .given().log().all()
                .queryParam("year", diary.getCreatedAt().getYear())
                .queryParam("month", diary.getCreatedAt().getMonth().getValue())
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryMonthlyResponse.class);

        assertThat(response.days()).hasSize(1);
        DiaryDayResponse day = response.days().get(0);
        assertThat(day.canView()).isTrue();
        assertThat(day.id()).isEqualTo(diary.getId());
        assertThat(day.day()).isEqualTo(diary.getCreatedAt().getDayOfMonth());
        assertThat(day.profileImage()).isEqualTo(this.member.getProfileImage());
    }

    @Test
    @DisplayName("사용자가 어제 일기까지 조회 가능")
    void 달력형_일기_조회_성공_조회_불가능한_일기() {
        Group group = createGroup();
        updateSelf(group, false);
        Diary diary = createDiary(group);

        DiaryMonthlyResponse response = RestAssured
                .given().log().all()
                .queryParam("year", diary.getCreatedAt().getYear())
                .queryParam("month", diary.getCreatedAt().getMonth().getValue())
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryMonthlyResponse.class);

        assertThat(response.days()).hasSize(1);
        DiaryDayResponse day = response.days().get(0);
        assertThat(day.canView()).isFalse();
        assertThat(day.id()).isEqualTo(diary.getId());
        assertThat(day.day()).isEqualTo(diary.getCreatedAt().getDayOfMonth());
        assertThat(day.profileImage()).isEqualTo(this.member.getProfileImage());
    }

    @Test
    void 달력형_일기_조회_실패_날짜_유효성_검사() {
        Group group = createGroup();
        updateSelf(group, true);

        RestAssured
                .given().log().all()
                .queryParam("year", "2024")
                .queryParam("month", "13")
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ErrorCode.INVALID_DATE.getMessage()));
    }

    private Group createGroup() {
        return groupRepository.save(Group.from("버니즈"));
    }

    private void updateSelf(Group group, boolean canViewToday) {
        this.member.joinGroup("nickname", "/image", 1, GroupRole.GROUP_LEADER, group);
        if (canViewToday) {
            member.updateLastViewableDiaryDate();
        }
        memberRepository.save(member);
    }

    private Diary createDiary(Group group) {
        Diary diary = Diary.builder()
                .todayMood("sleepy.svg")
                .group(group)
                .member(member)
                .build();
        return diaryRepository.save(diary);
    }
}
