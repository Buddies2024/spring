package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.ui.dto.response.DiaryWritableStatusResponse;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class DiaryWritableStatusApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%d/diaries/status";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    @DisplayName("일기 볼 수 있지만 작성은 아직 못 하는 상태 - 작성된 diaryId 값 반환")
    void 내_순서_오늘_일기_작성_완료() {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, GroupRole.GROUP_MEMBER, group);
        Member groupMember = createMemberInGroup(group);
        memberRepository.saveAll(Arrays.asList(member, groupMember));
        Diary diary = createDiary(group, groupMember);
        diaryRepository.save(diary);

        DiaryWritableStatusResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryWritableStatusResponse.class);

        assertThat(response.isMyOrder()).isEqualTo(true);
        assertThat(response.writtenTodayDiary()).isEqualTo(true);
        assertThat(response.viewableDiaryId()).isEqualTo(diary.getId());
    }

    @Test
    @DisplayName("내가 일기 작성 후 순서는 변한 상태 - 작성한 diaryId 값 반환")
    void 나의_오늘_일기_작성_완료_순서넘어감() {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 2, GroupRole.GROUP_MEMBER, group);
        memberRepository.save(member);
        Diary diary = createDiary(group, member);
        diaryRepository.save(diary);

        DiaryWritableStatusResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryWritableStatusResponse.class);

        assertThat(response.isMyOrder()).isEqualTo(false);
        assertThat(response.writtenTodayDiary()).isEqualTo(true);
        assertThat(response.viewableDiaryId()).isEqualTo(diary.getId());
    }

    @Test
    void 내_순서_오늘_일기_작성_미완료() {
        Group group = createGroup(1);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, GroupRole.GROUP_MEMBER, group);
        memberRepository.save(member);

        DiaryWritableStatusResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryWritableStatusResponse.class);

        assertThat(response.isMyOrder()).isEqualTo(true);
        assertThat(response.writtenTodayDiary()).isEqualTo(false);
        assertThat(response.viewableDiaryId()).isEqualTo(null);
    }

    @Test
    void 친구_순서_오늘_일기_작성_완료() {
        Group group = createGroup(2);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, GroupRole.GROUP_MEMBER, group);
        Member groupMember = createMemberInGroup(group);
        memberRepository.saveAll(Arrays.asList(member, groupMember));
        Diary diary = createDiary(group, groupMember);
        diaryRepository.save(diary);

        DiaryWritableStatusResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryWritableStatusResponse.class);

        assertThat(response.isMyOrder()).isEqualTo(false);
        assertThat(response.writtenTodayDiary()).isEqualTo(true);
        assertThat(response.viewableDiaryId()).isEqualTo(null);
    }

    @Test
    void 친구_순서_오늘_일기_작성_미완료() {
        Group group = createGroup(2);
        groupRepository.save(group);
        member.updateMemberGroupInfo("api요청멤버", "orange", 1, GroupRole.GROUP_MEMBER, group);
        Member groupMember = createMemberInGroup(group);
        memberRepository.saveAll(Arrays.asList(member, groupMember));

        DiaryWritableStatusResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryWritableStatusResponse.class);

        assertThat(response.isMyOrder()).isEqualTo(false);
        assertThat(response.writtenTodayDiary()).isEqualTo(false);
        assertThat(response.viewableDiaryId()).isEqualTo(null);
    }

    private Diary createDiary(Group group, Member member) {
        return Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .group(group)
                .member(member)
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
                .orderInGroup(2)
                .group(group)
                .build();
    }
}
