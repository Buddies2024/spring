package com.exchangediary.comment.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.domain.ReplyRepository;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.entity.Reply;
import com.exchangediary.comment.ui.dto.response.CommentResponse;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.global.exception.ErrorCode;
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
import static org.hamcrest.Matchers.equalTo;

public class CommentViewApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%s/diaries/%d/comments/%d";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;

    @Test
    @DisplayName("답글을 포함한 댓글 조회에 성공한다.")
    void When_CommentHasRepliesExpect_SuccessViewComment() {
        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);
        Comment comment = createComment(10, 10, 1, "댓글 조회 테스트 중!!", me, diary);
        Reply firstReply = createReply("테스트 잘 되니??", writer, comment);
        Reply secondReply = createReply("몰라 자고 싶은 거 같기도...", me, comment);

        CommentResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId(), diary.getId(), comment.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CommentResponse.class);

        assertThat(body.content()).isEqualTo(comment.getContent());
        assertThat(body.profileImage()).isEqualTo(me.getProfileImage());
        assertThat(body.replies()).hasSize(2);
        assertThat(body.replies().get(0).content()).isEqualTo(firstReply.getContent());
        assertThat(body.replies().get(0).profileImage()).isEqualTo(writer.getProfileImage());
        assertThat(body.replies().get(1).content()).isEqualTo(secondReply.getContent());
        assertThat(body.replies().get(1).profileImage()).isEqualTo(me.getProfileImage());
    }

    @Test
    @DisplayName("답글이 달리지 않은 댓글 조회에 성공한다.")
    void When_CommentHasNoReplyExpect_SuccessViewComment() {
        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);
        Comment comment = createComment(10, 10, 1, "댓글 조회 테스트 중!!", me, diary);

        CommentResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId(), diary.getId(), comment.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CommentResponse.class);

        assertThat(body.content()).isEqualTo(comment.getContent());
        assertThat(body.profileImage()).isEqualTo(me.getProfileImage());
        assertThat(body.replies()).hasSize(0);
    }

    @Test
    @DisplayName("id에 해당하는 댓글이 없는 경우 404 예외가 발생한다.")
    void When_NonExistentComment_Throw404Exception() {
        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);
        createComment(10, 10, 1, "댓글 조회 테스트 중!!", me, diary);

        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().get(String.format(API_PATH, group.getId(), diary.getId(), 1234L))
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo(ErrorCode.COMMENT_NOT_FOUND.getMessage()));
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

    private Comment createComment(double x, double y, int page, String content, GroupMember groupMember, Diary diary) {
        Comment comment = Comment.of(x, y, page, content, groupMember, diary);
        return commentRepository.save(comment);
    }

    private Reply createReply(String content, GroupMember groupMember, Comment comment) {
        Reply reply = Reply.of(content, groupMember, comment);
        return replyRepository.save(reply);
    }
}
