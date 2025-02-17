package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DiaryViewApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%s/diaries/%d";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void 일기_조회_성공() {
        Group group = createGroup();
        this.member.joinGroup("self", "red", 1, GroupRole.GROUP_MEMBER, group);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);
        Member diaryCreator = createMember(group);
        Diary diary = createDiary(diaryCreator, group);
        createDiaryContent(diary);
        createComment(member, diary);

        var response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryResponse.class);
        var diaryContents = response.contents();
        var diaryComments = response.comments();

        assertThat(diaryContents).hasSize(3);
        assertThat(diaryContents.get(0).content()).isEqualTo("hi");
        assertThat(diaryContents.get(1).content()).isEqualTo("hi");
        assertThat(diaryContents.get(2).content()).isEqualTo("hi");
        assertThat(response.createdAt()).isEqualTo(
                diary.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        assertThat(response.todayMood()).isEqualTo(diary.getTodayMood());
        assertThat(response.imageFileName()).isEqualTo("20241101.jpeg");
        assertThat(response.nickname()).isEqualTo("하니");
        assertThat(response.profileImage()).isEqualTo("orange");
        assertThat(diaryComments.getFirst().page()).isEqualTo(1);
        assertThat(diaryComments.getFirst().xCoordinate()).isEqualTo(123.45);
        assertThat(diaryComments.getFirst().yCoordinate()).isEqualTo(333.33);
        assertThat(diaryComments.getFirst().profileImage()).isEqualTo("red");
    }

    @Test
    void 일기_조회_성공_댓글_없을_경우() {
        Group group = createGroup();
        this.member.joinGroup("self", "red", 1, GroupRole.GROUP_MEMBER, group);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);
        Member diaryCreator = createMember(group);
        Diary diary = createDiary(diaryCreator, group);
        createDiaryContent(diary);

        var response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryResponse.class);
        var diaryContents = response.contents();
        var diaryComments = response.comments();

        assertThat(diaryContents).hasSize(3);
        assertThat(diaryContents.get(0).content()).isEqualTo("hi");
        assertThat(diaryContents.get(1).content()).isEqualTo("hi");
        assertThat(diaryContents.get(2).content()).isEqualTo("hi");
        assertThat(response.createdAt()).isEqualTo(
                diary.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        assertThat(response.todayMood()).isEqualTo(diary.getTodayMood());
        assertThat(response.imageFileName()).isEqualTo("20241101.jpeg");
        assertThat(response.nickname()).isEqualTo("하니");
        assertThat(response.profileImage()).isEqualTo("orange");
        assertThat(diaryComments).isEmpty();
    }

    @Test
    void 일기_조회_성공_댓글_여러개() {
        Group group = createGroup();
        this.member.joinGroup("self", "red", 1, GroupRole.GROUP_MEMBER, group);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);
        Member diaryCreator = createMember(group);
        Member commentCreator = createMember(group);
        Diary diary = createDiary(diaryCreator, group);
        createDiaryContent(diary);
        createComment(member, diary);
        createComment(commentCreator, diary);

        var response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryResponse.class);
        var diaryContents = response.contents();
        var diaryComments = response.comments();

        assertThat(diaryContents).hasSize(3);
        assertThat(diaryContents.get(0).content()).isEqualTo("hi");
        assertThat(diaryContents.get(1).content()).isEqualTo("hi");
        assertThat(diaryContents.get(2).content()).isEqualTo("hi");
        assertThat(response.createdAt()).isEqualTo(
                diary.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        assertThat(response.todayMood()).isEqualTo(diary.getTodayMood());
        assertThat(response.imageFileName()).isEqualTo("20241101.jpeg");
        assertThat(response.nickname()).isEqualTo("하니");
        assertThat(response.profileImage()).isEqualTo("orange");
        assertThat(diaryComments.getFirst().page()).isEqualTo(1);
        assertThat(diaryComments.getFirst().xCoordinate()).isEqualTo(123.45);
        assertThat(diaryComments.getFirst().yCoordinate()).isEqualTo(333.33);
        assertThat(diaryComments.getFirst().profileImage()).isEqualTo("red");
        assertThat(diaryComments.get(1).page()).isEqualTo(1);
        assertThat(diaryComments.get(1).xCoordinate()).isEqualTo(123.45);
        assertThat(diaryComments.get(1).yCoordinate()).isEqualTo(333.33);
        assertThat(diaryComments.get(1).profileImage()).isEqualTo("orange");
    }

    @Test
    void 일기_조회_실패_일기_없음 () {
        Group group = createGroup();
        this.member.joinGroup("self", "red", 1, GroupRole.GROUP_MEMBER, group);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);
        Long diaryId = 1L;

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format(API_PATH, group.getId(), diaryId))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 일기_조회_인가_실패() {
        Group group = createGroup();
        this.member.joinGroup("self", "red", 1, GroupRole.GROUP_MEMBER, group);
        memberRepository.save(member);
        Member diaryCreator = createMember(group);
        Diary diary = createDiary(diaryCreator, group);
        createDiaryContent(diary);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private Group createGroup() {
        return groupRepository.save(Group.from("GROUP_NAME"));
    }

    private Diary createDiary(Member member, Group group) {
        Diary diary = Diary.builder()
                .todayMood("sleepy.svg")
                .imageFileName("20241101.jpeg")
                .group(group)
                .member(member)
                .build();
        diaryRepository.save(diary);
        return diary;
    }

    private Member createMember(Group group) {
        Member member = Member.builder()
                .profileImage("orange")
                .kakaoId(1234L)
                .orderInGroup(2)
                .nickname("하니")
                .groupRole(GroupRole.GROUP_MEMBER)
                .group(group)
                .build();
        memberRepository.save(member);
        return member;
    }

    private void createDiaryContent(Diary diary) {
        List<DiaryContent> diaryContents = new ArrayList<>();
        int index = 0;

        while (index < 3) {
            diaryContents.add(DiaryContent.of(index + 1, "hi", diary));
            index++;
        }
        diaryContentRepository.saveAll(diaryContents);
    }

    private void createComment(Member member, Diary diary) {
        commentRepository.save(
                Comment.builder()
                        .xCoordinate(123.45)
                        .yCoordinate(333.33)
                        .page(1)
                        .content("댓글")
                        .member(member)
                        .diary(diary)
                        .build()
        );
    }
}
