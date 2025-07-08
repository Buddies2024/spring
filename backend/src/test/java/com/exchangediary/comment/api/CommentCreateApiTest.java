package com.exchangediary.comment.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.request.CommentCreateRequest;
import com.exchangediary.comment.ui.dto.response.CommentCreateResponse;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommentCreateApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries/%d/comments";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 작성에 성공한다.")
    void Expect_SuccessCreateComment() {
        // Given
        double xCoordinate = 12.34;
        double yCoordinate= 56.78;
        int page = 1;
        String content = "댓글 작성 테스트 중!";

        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);

        // When
        CommentCreateResponse body = RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(CommentCreateResponse.class);

        // Then
        Comment comment = commentRepository.findById(body.id()).get();
        assertThat(comment.getXCoordinate()).isEqualTo(xCoordinate);
        assertThat(comment.getYCoordinate()).isEqualTo(yCoordinate);
        assertThat(comment.getPage()).isEqualTo(page);

        assertThat(body.xCoordinate()).isEqualTo(xCoordinate);
        assertThat(body.yCoordinate()).isEqualTo(yCoordinate);
        assertThat(body.page()).isEqualTo(page);
        assertThat(body.profileImage()).isEqualTo(me.getProfileImage());
    }

    @Test
    @DisplayName("댓글은 반드시 내용을 포함해야 한다.")
    void When_EmptyContent_Expect_Throw400Exception() {
        // Given
        double xCoordinate = 12.34;
        double yCoordinate= 56.78;
        int page = 1;
        String content = "";

        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);

        // When
        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("댓글 내용을 입력해주세요."));
    }

    @Test
    @DisplayName("사용자가 해당 일기 작성자이면 댓글을 달 수 없다.")
    void When_MemberIsDiaryWriter_Expect_Throw403Exception() {
        // Given
        double xCoordinate = 12.34;
        double yCoordinate= 56.78;
        int page = 1;
        String content = "댓글 작성 테스트 중!";

        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember nextWriter = joinGroup("다음작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(me, group, List.of("오늘의 날씨 맑음 :)"), nextWriter);

        // When
        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo("내 일기에는 댓글을 남길 수 없어요!"));
    }

    @Test
    @DisplayName("해당 일기에 이미 댓글 작성했으면 또 댓글을 달 수 없다.")
    void When_AlreadyWriteCommentInDiary_Expect_Throw403Exception() {
        // Given
        double xCoordinate = 12.34;
        double yCoordinate= 56.78;
        int page = 1;
        String content = "댓글 작성 테스트 중!";

        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);

        // When
        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo("댓글은 한 번 남길 수 있어요!"));
    }

    @Test
    @DisplayName("사용자가 일기를 조회할 수 없으면 댓글도 달 수 없다.")
    void When_MemberCannotViewDiary_Expect_Throw403Exception() {
        // Given
        double xCoordinate = 12.34;
        double yCoordinate= 56.78;
        int page = 1;
        String content = "댓글 작성 테스트 중!";

        Group group = createGroup();
        joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        GroupMember nextWriter = joinGroup("다음작성자", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), nextWriter);

        // When
        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo(ErrorCode.DIARY_VIEW_FORBIDDEN.getMessage()));
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
