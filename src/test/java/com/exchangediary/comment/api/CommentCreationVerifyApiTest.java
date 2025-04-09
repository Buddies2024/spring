package com.exchangediary.comment.api;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.response.CommentCreationVerifyResponse;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommentCreationVerifyApiTest extends ApiBaseTest {
    private static final String URI = "/api/groups/%s/diaries/%d/comments/verify";

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryContentRepository diaryContentRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글창 생성에 성공한다.")
    void Expect_CanCreateComment() {
        // Given
        Group group = createGroup();
        GroupMember me = joinGroup("스프링", 0, GroupRole.GROUP_LEADER, group, member);
        GroupMember writer = joinGroup("작성자", 1, GroupRole.GROUP_MEMBER, group, createMember(2L));
        Diary diary = writeDiary(writer, group, List.of("오늘의 날씨 맑음 :)"), me);

        CommentCreationVerifyResponse body = RestAssured
                .given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().get(String.format(URI, group.getId(), diary.getId()))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CommentCreationVerifyResponse.class);

        assertThat(body.profileImage()).isEqualTo(me.getProfileImage());
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
