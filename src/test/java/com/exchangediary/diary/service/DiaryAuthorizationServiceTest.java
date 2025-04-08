package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiaryAuthorizationServiceTest {
    @InjectMocks
    public DiaryAuthorizationService diaryAuthorizationService;
    @Mock
    private GroupMemberQueryService groupMemberQueryService;
    @Mock
    private DiaryRepository diaryRepository;

    @Test
    @DisplayName("일기 작성순서이고 오늘 작성된 일기가 없으면, 일기를 작성할 수 있다.")
    public void When_MemberHasCurrentOrderAndNotExistWrittenTodayDiary_Expect_CanWriteDiary() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        Member member = Member.from(1L);
        GroupMember groupMember = GroupMember.of("스프링", "red", 1, GroupRole.GROUP_LEADER, group, member);

        when(diaryRepository.existsByGroupAndDate(group.getId(), LocalDate.now())).thenReturn(false);

        // When & Then
        diaryAuthorizationService.checkDiaryWritable(group, groupMember);
    }

    @Test
    @DisplayName("일기 작성순서가 아닌 경우 403 예외를 발생한다.")
    public void When_MemberIsNotCurrentOrderAndNotExistWrittenTodayDiary_Expect_Throw403Exception() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        Member member = Member.from(1L);
        GroupMember groupMember = GroupMember.of("스프링", "red", 2, GroupRole.GROUP_LEADER, group, member);

        // When & Then
        assertThrows(ForbiddenException.class, () -> diaryAuthorizationService.checkDiaryWritable(group, groupMember));
    }

    @Test
    @DisplayName("오늘 작성된 일기가 있는 경우 403 예외를 발생한다.")
    public void When_MemberHasCurrentOrderAndExistWrittenTodayDiary_Expect_Throw403Exception() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        Member member = Member.from(1L);
        GroupMember groupMember = GroupMember.of("스프링", "red", 1, GroupRole.GROUP_LEADER, group, member);

        when(diaryRepository.existsByGroupAndDate(group.getId(), LocalDate.now())).thenReturn(true);

        // When & Then
        assertThrows(ForbiddenException.class, () -> diaryAuthorizationService.checkDiaryWritable(group, groupMember));
    }

    @Test
    @DisplayName("사용자의 마지막 조회 가능 날짜보다 일기 작성이 먼저 되거나 같으면, 일기를 조회할 수 있다.")
    public void When_MemberLastViewableDiaryDateIsAfterThanDiaryCreated_Expect_CanViewDiary() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        Member member = Member.from(1L);
        GroupMember groupMember = GroupMember.of("스프링", "red", 1, GroupRole.GROUP_LEADER, group, member);

        Diary diary = Diary.of("happy", groupMember, group);
        ReflectionTestUtils.setField(diary, "createdAt", LocalDateTime.now());

        // When & Then
        diaryAuthorizationService.checkDiaryViewable(LocalDate.now(), diary);
        diaryAuthorizationService.checkDiaryViewable(LocalDate.now().plusDays(1), diary);
    }

    @Test
    @DisplayName("사용자의 마지막 조회 가능 날짜보다 일기 작성이 나중에 되었으면 일기를 조회할 수 없다.")
    public void When_MemberLastViewableDiaryDateIsBeforeThanDiaryCreated_Expect_Throw403Exception() {
        // Given
        Group group = Group.from("버디즈");
        ReflectionTestUtils.setField(group, "createdAt", LocalDateTime.now());
        Member member = Member.from(1L);
        GroupMember groupMember = GroupMember.of("스프링", "red", 1, GroupRole.GROUP_LEADER, group, member);

        Diary diary = Diary.of("happy", groupMember, group);
        ReflectionTestUtils.setField(diary, "createdAt", LocalDateTime.now());

        // When & Then
        assertThrows(ForbiddenException.class, () -> diaryAuthorizationService.checkDiaryViewable(LocalDate.now().minusDays(1), diary));
    }
}
