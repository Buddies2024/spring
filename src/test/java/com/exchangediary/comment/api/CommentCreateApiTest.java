package com.exchangediary.comment.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.request.CommentCreateRequest;
import com.exchangediary.comment.ui.dto.response.CommentCreateResponse;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommentCreateApiTest extends ApiBaseTest {
    private static final String API_PATH = "/api/groups/%s/diaries/%d/comments";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void 댓글_작성_성공() {
        double xCoordinate = 123.45;
        double yCoordinate= 456.78;
        int page = 1;
        String content = "댓글";

        Group group = createGroup();
        Member diaryCreator = createMember(group);
        Diary diary = createDiary(group, diaryCreator);
        updateSelf(group, 1);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);

        var response = RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(CommentCreateResponse.class);

        Comment comment = commentRepository.findById(response.id()).get();
        assertThat(comment.getXCoordinate()).isEqualTo(xCoordinate);
        assertThat(comment.getYCoordinate()).isEqualTo(yCoordinate);
        assertThat(comment.getPage()).isEqualTo(page);
        assertThat(comment.getContent()).isEqualTo(content);
    }

    @Test
    void 댓글_작성_실패_내용_없을_경우() {
        double xCoordinate = 123.45;
        double yCoordinate= 456.78;
        int page = 1;
        String content = "";

        Group group = createGroup();
        Member diaryCreator = createMember(group);
        Diary diary = createDiary(group, diaryCreator);
        updateSelf(group, 1);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);

        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 댓글_작성_실패_일기작성자일경우() {
        double xCoordinate = 123.45;
        double yCoordinate= 456.78;
        int page = 1;
        String content = "댓글";

        Group group = createGroup();
        updateSelf(group, 1);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);
        Diary diary = createDiary(group, member);

        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void 댓글_작성_실패_이미작성() {
        double xCoordinate = 123.45;
        double yCoordinate= 456.78;
        int page = 1;
        String content = "댓글";

        Group group = createGroup();
        Member diaryCreator = createMember(group);
        Diary diary = createDiary(group, diaryCreator);
        updateSelf(group, 1);
        this.member.updateLastViewableDiaryDate();
        memberRepository.save(member);
        createComment(member, diary);

        RestAssured
                .given().log().all()
                .body(new CommentCreateRequest(xCoordinate, yCoordinate, page, content))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post(String.format(API_PATH, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private Group createGroup() {
        return groupRepository.save(Group.from("GROUP_NAME"));
    }

    private void updateSelf(Group group, int order) {
        member.joinGroup("api요청멤버", "orange", order, GroupRole.GROUP_MEMBER, group);
        memberRepository.save(member);
    }

    private Diary createDiary(Group group, Member member) {
        Diary diary = Diary.builder()
                .todayMood("sleepy.svg")
                .group(group)
                .member(member)
                .build();
        return diaryRepository.save(diary);
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
