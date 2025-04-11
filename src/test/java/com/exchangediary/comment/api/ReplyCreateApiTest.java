package com.exchangediary.comment.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.request.ReplyCreateRequest;
import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
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

import static org.hamcrest.Matchers.equalTo;

public class ReplyCreateApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries/%d/comments/%d/replies";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("답글 작성에 성공한다.")
    void Expect_SuccessCreateReply() {
        // Given
        String content = "답글 작성 테스트 중!";

        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);
        Comment comment = createComment(10, 10, 1, "댓글 작성 완료", me, diary);

        // When & Then
        RestAssured
                .given().log().all()
                .body(new ReplyCreateRequest(content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId(), comment.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("댓글은 반드시 내용을 포함해야 한다.")
    void When_EmptyContent_Expect_Throw400Exception() {
        // Given
        String content = "";

        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);
        Comment comment = createComment(10, 10, 1, "댓글 작성 완료", me, diary);

        // When & Then
        RestAssured
                .given().log().all()
                .body(new ReplyCreateRequest(content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(URI, group.getId(), diary.getId(), comment.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("답글 내용을 입력해주세요."));
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
}
