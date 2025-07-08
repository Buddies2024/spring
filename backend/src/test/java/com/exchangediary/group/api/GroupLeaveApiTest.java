package com.exchangediary.group.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.domain.ReplyRepository;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.entity.Reply;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupLeaveApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/leave";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;

    /*
    그룹원 탈퇴 시,
    1. 그룹원이 작성한 일기, 댓글, 답글이 모두 삭제된다.
    2. 그룹의 정보가 변경된다.
        1) 그룹원 수 (memberCount)
        2) 현재 순서 (currentOrder) - 현재 순서가 다른 그룹원으로 바뀌면, 해당 그룹원의 lastViewableDiaryDate을 갱신해야 한다.
    3. 남은 그룹원들의 순서가 변경된다. (orderInGroup)
    */

    @Test
    @DisplayName("탈퇴하는 그룹원이 작성한 그룹 내 일기, 댓글, 답글이 모두 삭제된다.")
    void When_LeaveMemberWriteDiaryCommentReplyInGroup_Expect_DeleteEverything() {
        // Given
        Group group = createGroup();
        GroupMember member1 = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember me = joinGroup("스프링", 1, GroupRole.GROUP_MEMBER, group, member);
        GroupMember member2 = joinGroup("멤버", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));

        writeDiary(me, group, List.of("오늘의 날씨 맑음 :)"), member2);
        Diary diary = writeDiary(member2, group, List.of("오늘의 날씨 맑음 :)"), member1);
        Comment comment = createComment(10, 10, 1, "탈퇴 테스트 중!", me, diary);
        createReply("굿굿b", me, comment);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);

        boolean existsMember = groupMemberRepository.existsById(me.getId());
        assertThat(existsMember).isFalse();

        List<Diary> myDiaries = diaryRepository.findByMemberId(member.getId());
        assertThat(myDiaries).hasSize(0);

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(0);

        List<Reply> replies = replyRepository.findAll();
        assertThat(replies).hasSize(0);
    }

    @Test
    @DisplayName("탈퇴하는 그룹원의 순서가 현재 그룹 순서보다 늦다면, 현재 순서의 변동은 없다.")
    void When_LeaveMembersOrderIsFasterThanGroupCurrentOrder_Expect_NotChangeCurrentOrder() {
        // Given
        Group group = createGroup();
        GroupMember member1 = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember me = joinGroup("스프링", 1, GroupRole.GROUP_MEMBER, group, member);
        GroupMember member2 = joinGroup("멤버", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));

        group.changeCurrentOrder(1);
        groupRepository.save(group);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        boolean existsMember = groupMemberRepository.existsById(me.getId());
        assertThat(existsMember).isFalse();

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);

        GroupMember updatedMember1 = groupMemberRepository.findById(member1.getId()).get();
        assertThat(updatedMember1.getOrderInGroup()).isEqualTo(1);

        GroupMember updatedMember2 = groupMemberRepository.findById(member2.getId()).get();
        assertThat(updatedMember2.getOrderInGroup()).isEqualTo(2);
        assertThat(updatedMember2.getLastViewableDiaryDate()).isNotEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("탈퇴하는 그룹원의 순서가 현재 그룹 순서보다 빠르다면, 현재 그룹 순서가 1 감소한다.")
    void When_LeaveMembersOrderIsFasterThanGroupCurrentOrder_Expect_GroupCurrentOrderDecrease1() {
        // Given
        Group group = createGroup();
        GroupMember member1 = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember me = joinGroup("스프링", 1, GroupRole.GROUP_MEMBER, group, member);
        GroupMember member2 = joinGroup("멤버", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));

        group.changeCurrentOrder(3);
        groupRepository.save(group);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        boolean existsMember = groupMemberRepository.existsById(me.getId());
        assertThat(existsMember).isFalse();

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(2);
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);

        GroupMember updatedMember1 = groupMemberRepository.findById(member1.getId()).get();
        assertThat(updatedMember1.getOrderInGroup()).isEqualTo(1);

        GroupMember updatedMember2 = groupMemberRepository.findById(member2.getId()).get();
        assertThat(updatedMember2.getOrderInGroup()).isEqualTo(2);
        assertThat(updatedMember2.getLastViewableDiaryDate()).isNotEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("""
        탈퇴하는 그룹원의 순서와 현재 그룹 순서가 동일하고 그룹원 수와도 같다면, 그룹 순서는 1로 변경된다.
        또한 새로운 일기 작성자는 모든 일기를 볼 수 있다.
    """)
    void When_LeaveMembersOrderIsSameWithGroupCurrentOrderAndSameWithGroupMemberCount_Expect_SuccessLeaveGroup() {
        // Given
        Group group = createGroup();
        GroupMember member1 = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember member2 = joinGroup("멤버", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));
        GroupMember me = joinGroup("스프링", 1, GroupRole.GROUP_MEMBER, group, member);

        group.changeCurrentOrder(3);
        groupRepository.save(group);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        boolean existsMember = groupMemberRepository.existsById(me.getId());
        assertThat(existsMember).isFalse();

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(1);
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);

        GroupMember updatedMember1 = groupMemberRepository.findById(member1.getId()).get();
        assertThat(updatedMember1.getOrderInGroup()).isEqualTo(1);
        assertThat(updatedMember1.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());

        GroupMember updatedMember2 = groupMemberRepository.findById(member2.getId()).get();
        assertThat(updatedMember2.getOrderInGroup()).isEqualTo(2);
    }

    @Test
    @DisplayName("""
        탈퇴하는 그룹원의 순서와 현재 그룹 순서가 동일하면, 그룹 순서는 동일하다.
        또한 일기 작성자가 변경되고 모든 일기를 볼 수 있다.
    """)
    void When_LeaveMembersOrderIsSameGroupCurrentOrder_Expect_SuccessLeaveGroup() {
        // Given
        Group group = createGroup();
        GroupMember member1 = joinGroup("리더", 0, GroupRole.GROUP_LEADER, group, createMember(2L));
        GroupMember me = joinGroup("스프링", 1, GroupRole.GROUP_MEMBER, group, member);
        GroupMember member2 = joinGroup("멤버", 2, GroupRole.GROUP_MEMBER, group, createMember(3L));

        group.changeCurrentOrder(2);
        groupRepository.save(group);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        boolean existsMember = groupMemberRepository.existsById(me.getId());
        assertThat(existsMember).isFalse();

        Group updatedGroup = groupRepository.findById(group.getId()).get();
        assertThat(updatedGroup.getCurrentOrder()).isEqualTo(2);
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);

        GroupMember updatedMember1 = groupMemberRepository.findById(member1.getId()).get();
        assertThat(updatedMember1.getOrderInGroup()).isEqualTo(1);

        GroupMember updatedMember2 = groupMemberRepository.findById(member2.getId()).get();
        assertThat(updatedMember2.getOrderInGroup()).isEqualTo(2);
        assertThat(updatedMember2.getLastViewableDiaryDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("방장은 그룹을 탈퇴할 수 없다.")
    void When_MemberIsGroupLeader_Expect_Throw403Exception() {
        // Given
        Group group = createGroup();
        joinGroup("리더", 1, GroupRole.GROUP_LEADER, group, member);
        joinGroup("그룹원", 0, GroupRole.GROUP_MEMBER, group, createMember(2L));

        // When & Then
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo(ErrorCode.GROUP_LEADER_LEAVE_FORBIDDEN.getMessage()));
    }


    @Test
    @DisplayName("그룹원의 수가 한 명이고 이 그룹원이 탈퇴하면, 그룹은 삭제된다.")
    void When_GroupHas1Member_Expect_DeleteGroup() {
        // Given
        Group group = createGroup();
        GroupMember leader = joinGroup("리더", 1, GroupRole.GROUP_LEADER, group, member);

        // When
        RestAssured
                .given().log().all()
                .cookie("token", token)
                .when().patch(String.format(URI, group.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // Then
        boolean existsLeader = groupMemberRepository.existsById(leader.getId());
        assertThat(existsLeader).isFalse();

        boolean existsGroup = groupRepository.existsById(group.getId());
        assertThat(existsGroup).isFalse();
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
