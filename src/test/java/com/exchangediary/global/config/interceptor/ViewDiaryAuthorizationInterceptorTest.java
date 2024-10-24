package com.exchangediary.global.config.interceptor;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class ViewDiaryAuthorizationInterceptorTest extends ApiBaseTest {
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private GroupRepository groupRepository;


    @Test
    void 일기_조회_성공() {
        Group group = createGroup();
        updateSelf(group, LocalDate.now());
        Diary diary = createDiary(this.member, group);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format("/group/%d/diary/%d", group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 일기_조회_실패() {
        Group group = createGroup();
        updateSelf(group, LocalDate.now().minusDays(1));
        Diary diary = createDiary(this.member, group);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format("/group/%d/diary/%d", group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FOUND.value());
    }

    private Group createGroup() {
        Group group = Group.of("groupname", "code");
        return groupRepository.save(group);
    }

    private void updateSelf(Group group, LocalDate lastViewableDiaryDate) {
        this.member.joinGroup("nickname", "red", 1, GroupRole.GROUP_MEMBER, group);
        this.member.updateLastViewableDiaryDate(lastViewableDiaryDate);
        memberRepository.save(this.member);
    }

    private Diary createDiary(Member member, Group group) {
        Diary diary = Diary.builder()
                .content("content")
                .moodLocation("mood-location")
                .member(member)
                .group(group)
                .build();
        return diaryRepository.save(diary);
    }
}
