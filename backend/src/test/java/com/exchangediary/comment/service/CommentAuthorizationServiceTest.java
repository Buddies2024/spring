package com.exchangediary.comment.service;

import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentAuthorizationServiceTest {
    @InjectMocks
    public CommentAuthorizationService commentAuthorizationService;
    @Mock
    public CommentRepository commentRepository;

    @Test
    @DisplayName("댓글을 작성하려는 일기의 작성자인 경우 댓글을 작성할 수 없다.")
    void When_MemberIsDiaryWriter_Expect_Throw403Exception() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        GroupMember writer = GroupMember.of("스프링", "red", 2, GroupRole.GROUP_LEADER, group, Member.from(1L));
        Diary diary = Diary.of("happy.png", writer, group);

        // When & Then
        assertThrows(ForbiddenException.class, () -> commentAuthorizationService.checkCommentWritable(writer, diary));
    }

    @Test
    @DisplayName("댓글을 이미 작성한 경우 댓글을 작성할 수 없다.")
    void When_AlreadyWriteComment_Expect_Throw403Exception() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        GroupMember me = GroupMember.of("스프링", "red", 2, GroupRole.GROUP_LEADER, group, Member.from(1L));
        GroupMember writer = GroupMember.of("스프링", "red", 2, GroupRole.GROUP_LEADER, group, Member.from(2L));
        Diary diary = Diary.of("happy.png", writer, group);

        when(commentRepository.existsByGroupMemberIdAndDiaryId(me.getId(), diary.getId())).thenReturn(true);

        // When & Then
        assertThrows(ForbiddenException.class, () -> commentAuthorizationService.checkCommentWritable(me, diary));
    }

    @Test
    @DisplayName("일기 작성자가 아니고, 댓글을 작성하지 않은 경우 댓글을 작성할 수 있다.")
    void When_NotDiaryWriterAndNotWriteComment_Expect_CanWriteComment() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        GroupMember me = GroupMember.of("스프링", "red", 2, GroupRole.GROUP_LEADER, group, Member.from(1L));
        GroupMember writer = GroupMember.of("스프링", "red", 2, GroupRole.GROUP_LEADER, group, Member.from(2L));
        Diary diary = Diary.of("happy.png", writer, group);

        when(commentRepository.existsByGroupMemberIdAndDiaryId(me.getId(), diary.getId())).thenReturn(false);

        // When & Then
        commentAuthorizationService.checkCommentWritable(me, diary);
    }
}
