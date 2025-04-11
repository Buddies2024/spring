package com.exchangediary.diary.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.diary.ui.dto.response.DiaryContentResponse;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DiaryViewApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries/%d";
    private static final String TODAY_MOOD = "happy.png";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글이 없는 경우 일기 조회에 성공한다.")
    void When_NotIncludeComment_Expect_SuccessViewDiary() {
        // Given
        Group group = createGroup();
        GroupMember groupMember = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        groupMember.updateLastViewableDiaryDate();
        groupMemberRepository.save(groupMember);

        List<String> contents = List.of("오늘의", "날씨", "맑음", ":)");
        Diary diary = writeDiary(groupMember, group, contents);

        // When
        DiaryResponse response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryResponse.class);


        // Then
        assertThat(response.createdAt()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        assertThat(response.todayMood()).isEqualTo(TODAY_MOOD);
        assertThat(response.imageFileName()).isNull();
        assertThat(response.nickname()).isEqualTo("스프링");
        assertThat(response.profileImage()).isEqualTo(PROFILE_IMAGES[0]);
        assertThat(response.comments()).hasSize(0);

        List<DiaryContentResponse> diaryContents = response.contents();
        assertThat(diaryContents).hasSize(4);
        assertThat(diaryContents.get(0).content()).isEqualTo(contents.get(0));
        assertThat(diaryContents.get(1).content()).isEqualTo(contents.get(1));
        assertThat(diaryContents.get(2).content()).isEqualTo(contents.get(2));
        assertThat(diaryContents.get(3).content()).isEqualTo(contents.get(3));
    }

    @Test
    @DisplayName("댓글이 있는 경우 일기 조회에 성공한다.")
    void When_IncludeComment_Expect_SuccessViewDiary() {
        // Given
        Group group = createGroup();
        GroupMember groupMember = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        groupMember.updateLastViewableDiaryDate();
        groupMemberRepository.save(groupMember);

        List<String> contents = List.of("오늘의", "날씨", "맑음", ":)");
        Diary diary = writeDiary(groupMember, group, contents);
        createComment(10, 10, 1, "첫번째 댓글", groupMember, diary);
        createComment(20, 20, 2, "두번째 댓글", groupMember, diary);

        // When
        DiaryResponse response = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DiaryResponse.class);

        // Then
        assertThat(response.createdAt()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        assertThat(response.todayMood()).isEqualTo(TODAY_MOOD);
        assertThat(response.imageFileName()).isNull();
        assertThat(response.nickname()).isEqualTo("스프링");
        assertThat(response.profileImage()).isEqualTo(PROFILE_IMAGES[0]);
        assertThat(response.comments()).hasSize(2);

        List<DiaryContentResponse> diaryContents = response.contents();
        assertThat(diaryContents).hasSize(4);
        assertThat(diaryContents.get(0).content()).isEqualTo(contents.get(0));
        assertThat(diaryContents.get(1).content()).isEqualTo(contents.get(1));
        assertThat(diaryContents.get(2).content()).isEqualTo(contents.get(2));
        assertThat(diaryContents.get(3).content()).isEqualTo(contents.get(3));
    }

    @Test
    @DisplayName("id에 해당하는 일기 없는 경우, 404 예외를 반환한다.")
    void When_NonExistentDiaryMappingId_Expect_Throw404Exception() {
        // Given
        Group group = createGroup();
        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);

        Long diaryId = 1L;

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .redirects().follow(false)
                .when().get(String.format(URI, group.getId(), diaryId))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private Diary writeDiary(GroupMember groupMember, Group group, List<String> contents) {
        Diary diary = Diary.of(TODAY_MOOD, groupMember, group);
        diaryRepository.save(diary);

        for (int idx = 0; idx < contents.size(); idx++) {
            DiaryContent diaryContent = DiaryContent.of(idx + 1, contents.get(idx), diary);
            diaryContentRepository.save(diaryContent);
        }
        return diary;
    }

    private Comment createComment(double xCoordinate, double yCoordinate, int page, String content, GroupMember groupMember, Diary diary) {
        Comment comment = Comment.of(xCoordinate, yCoordinate, page, content, groupMember, diary);
        return commentRepository.save(comment);
    }
}
