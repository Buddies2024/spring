package com.exchangediary.diary.service;

import com.exchangediary.ApiBaseTest;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class DiaryQueryServiceTest extends ApiBaseTest {
    @Autowired
    private DiaryQueryService diaryQueryService;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Test
    void 일기_조회_성공() {
        Group group = createGroup();
        updateSelf(group, true);
        Diary diary = createDiary(this.member, group);

        DiaryResponse response = diaryQueryService.viewDiary(member.getId(), diary.getId());

        assertThat(response.createdAt()).isEqualTo(
                diary.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        assertThat(response.todayMood()).isEqualTo(diary.getTodayMood());
    }

    @Test
    void 일기_조회_실패_일기_없음() {
        createGroup();
        Long diaryId = 1L;

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                diaryQueryService.viewDiary(member.getId(), diaryId)
        );

        assertThat(exception.getValue()).isEqualTo(diaryId.toString());
    }

    @Test
    void 일기_조회_인가_실패() {
        Group group = createGroup();
        updateSelf(group, false);
        Diary diary = createDiary(this.member, group);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->
                diaryQueryService.viewDiary(member.getId(), diary.getId())
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.DIARY_VIEW_FORBIDDEN.getMessage());
    }

    private Group createGroup() {
        Group group = Group.from("버니즈");
        return groupRepository.save(group);
    }

    private void updateSelf(Group group, boolean canViewToday) {
        this.member.joinGroup("nickname", "red", 1, GroupRole.GROUP_MEMBER, group);
        if (canViewToday) {
            member.updateLastViewableDiaryDate();
        }
        memberRepository.save(this.member);
    }

    private Diary createDiary(Member member, Group group) {
        Diary diary = Diary.builder()
                .todayMood("sleepy.svg")
                .group(group)
                .member(member)
                .build();
        return diaryRepository.save(diary);
    }
}
