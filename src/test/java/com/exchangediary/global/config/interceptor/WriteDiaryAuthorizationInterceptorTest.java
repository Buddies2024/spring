package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;


public class WriteDiaryAuthorizationInterceptorTest extends ApiBaseTest {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void 일기_작성_권한_허가_성공() {
        Group group = createGroup();
        updateSelf(group, 1);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format("/group/%d/diary", group.getId()))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    void 일기_작성_권한_허가_실패_내_순서아님() {
        Group group = createGroup();
        updateSelf(group, 2);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format("/group/%d/diary", group.getId()))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FOUND.value());
    }

    @Test
    void 일기_작성_권한_허가_실패_오늘_일기_작성됨() {
        Group group = createGroup();
        updateSelf(group, 1);
        createDiary(group);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format("/group/%d/diary", group.getId()))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FOUND.value());
    }

    @Test
    void 일기_작성_API_권한_허가_실패_내_순서아님() {
        Group group = createGroup();
        updateSelf(group, 2);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format("/api/groups/%d/diaries", group.getId()))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void 일기_작성_API_권한_허가_실패_오늘_일기_작성됨() {
        Group group = createGroup();
        updateSelf(group, 1);
        createDiary(group);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format("/api/groups/%d/diaries", group.getId()))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void 일기_id_조회_요청_정상_작동_확인() {
        Group group = createGroup();
        updateSelf(group, 2);
        createDiary(group);
        LocalDate today = LocalDate.now();

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format("/group/%d/diary?year=%d&month=%d&day=%d", group.getId(), today.getYear(), today.getMonthValue(), today.getDayOfMonth()))
                .then()
                .log().status()
                .log().headers()
                .statusCode(HttpStatus.OK.value());
    }

    private Group createGroup() {
        return groupRepository.save(Group.of("group-name", "code"));
    }

    private void updateSelf(Group group, int order) {
        this.member.joinGroup(
                "me",
                "red",
                order,
                GroupRole.GROUP_MEMBER,
                group
        );
        memberRepository.save(this.member);
    }

    private void createDiary(Group group) {
        Diary diary = Diary.builder()
                .content("내용")
                .moodLocation("happy")
                .member(this.member)
                .group(group)
                .build();
        diaryRepository.save(diary);
    }
}
