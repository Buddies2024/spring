package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.diary.ui.dto.response.TodayDiaryStatusResponse;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodayDiaryStatusApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries/today";
    private static final String TODAY_MOOD = "happy.png";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;

    @Test
    @DisplayName("내 순서이고, 오늘 작성된 일기가 없는 경우")
    public void When_MyOrderAndNotExistWrittenDiaryToday() {
        Group group = createGroup();
        joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        joinGroup("나", 0, GroupRole.GROUP_MEMBER, group, member);
        joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(3L));
        group.changeCurrentOrder(2);
        groupRepository.save(group);

        TodayDiaryStatusResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TodayDiaryStatusResponse.class);

        assertThat(body.isMyOrder()).isTrue();
        assertThat(body.todayDiaryId()).isNull();
        assertThat(body.canViewTodayDiary()).isFalse();
    }

    @Test
    @DisplayName("내 순서이고, 오늘 작성된 일기가 있는 경우")
    public void When_MyOrderAndExistWrittenDiaryToday() {
        Group group = createGroup();
        GroupMember writer = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember nextWriter = joinGroup("나", 0, GroupRole.GROUP_MEMBER, group, member);
        joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(3L));
        writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), nextWriter);

        TodayDiaryStatusResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TodayDiaryStatusResponse.class);

        assertThat(body.isMyOrder()).isTrue();
        assertThat(body.todayDiaryId()).isNotNull();
        assertThat(body.canViewTodayDiary()).isTrue();
    }

    @Test
    @DisplayName("내 순서가 아니고, 오늘 작성된 일기가 내 일기인 경우")
    public void When_NotMyOrderAndExistWrittenDiaryTodayWhichMine() {
        Group group = createGroup();
        joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember writer = joinGroup("나", 0, GroupRole.GROUP_MEMBER, group, member);
        GroupMember nextWriter = joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(3L));
        writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), nextWriter);

        TodayDiaryStatusResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TodayDiaryStatusResponse.class);

        assertThat(body.isMyOrder()).isFalse();
        assertThat(body.todayDiaryId()).isNotNull();
        assertThat(body.canViewTodayDiary()).isTrue();
    }

    @Test
    @DisplayName("내 순서가 아니고, 오늘 작성된 일기가 내 일기가 아닌 경우")
    public void When_NotMyOrderAndExistWrittenDiaryTodayWhichNotMine() {
        Group group = createGroup();
        GroupMember nextWriter = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        joinGroup("나", 0, GroupRole.GROUP_MEMBER, group, member);
        GroupMember writer = joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(3L));
        writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), nextWriter);

        TodayDiaryStatusResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TodayDiaryStatusResponse.class);

        assertThat(body.isMyOrder()).isFalse();
        assertThat(body.todayDiaryId()).isNotNull();
        assertThat(body.canViewTodayDiary()).isFalse();
    }


    @Test
    @DisplayName("내 순서가 아니고, 오늘 작성된 일기가 없는 경우")
    public void When_NotMyOrderAndNotExistWrittenDiaryToday() {
        Group group = createGroup();
        joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        joinGroup("나", 0, GroupRole.GROUP_MEMBER, group, member);
        joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(3L));

        TodayDiaryStatusResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TodayDiaryStatusResponse.class);

        assertThat(body.isMyOrder()).isFalse();
        assertThat(body.todayDiaryId()).isNull();
        assertThat(body.canViewTodayDiary()).isFalse();
    }

    private Diary writeDiary(GroupMember writer, Group group, List<String> contents, GroupMember nextWriter) {
        Diary diary = Diary.of(TODAY_MOOD, writer, group);
        diaryRepository.save(diary);

        for (int idx = 0; idx < contents.size(); idx++) {
            DiaryContent diaryContent = DiaryContent.of(idx + 1, contents.get(idx), diary);
            diaryContentRepository.save(diaryContent);
        }

        writer.updateLastViewableDiaryDate();
        groupMemberRepository.save(writer);
        nextWriter.updateLastViewableDiaryDate();
        groupMemberRepository.save(nextWriter);
        group.changeCurrentOrder(nextWriter.getOrderInGroup());
        groupRepository.save(group);
        return diary;
    }
}
