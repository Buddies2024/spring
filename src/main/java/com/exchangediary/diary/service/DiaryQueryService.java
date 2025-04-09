package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.dto.DiaryInMonthly;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.ui.dto.response.TodayDiaryStatusResponse;
import com.exchangediary.diary.ui.dto.response.DiaryMonthlyResponse;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.InvalidDateException;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.group.service.GroupQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryQueryService {
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final DiaryRepository diaryRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupQueryService groupQueryService;

    public Diary findDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.DIARY_NOT_FOUND,
                        "",
                        String.valueOf(diaryId))
                );
    }

    public DiaryResponse viewDiary(Long memberId, Long diaryId) {
        Diary diary = findDiary(diaryId);
        LocalDate lastViewableDiaryDate = groupMemberQueryService.getLastViewableDiaryDate(memberId);

        diaryAuthorizationService.checkDiaryViewable(lastViewableDiaryDate, diary);

        return DiaryResponse.of(diary);
    }

    public DiaryMonthlyResponse viewMonthlyDiary(int year, int month, String groupId, Long memberId) {
        checkYearMonthFormat(year, month);

        List<DiaryInMonthly> diaries = diaryRepository.findDiaryInMonthlyByGroupIdAndYearAndMonth(groupId, year, month);
        LocalDate lastViewableDiaryDate = groupMemberQueryService.getLastViewableDiaryDate(memberId);
        return DiaryMonthlyResponse.of(diaries, lastViewableDiaryDate);
    }

    public TodayDiaryStatusResponse getTodayDiaryStatus(String groupId, Long memberId) {
        Group group = groupQueryService.findGroup(groupId);
        GroupMember groupMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);

        boolean isMyOrder = group.getCurrentOrder().equals(groupMember.getOrderInGroup());
        Long diaryId = null;
        boolean canViewTodayDiary = false;

        Optional<Diary> maybeTodayDiary = diaryRepository.findDiaryByGroupIdAndDate(groupId, LocalDate.now());
        if (maybeTodayDiary.isPresent()) {
            Diary todayDiary = maybeTodayDiary.get();

            diaryId = todayDiary.getId();
            canViewTodayDiary = !groupMember.getLastViewableDiaryDate().isBefore(todayDiary.getCreatedAt().toLocalDate());
        }
        return TodayDiaryStatusResponse.of(isMyOrder, diaryId, canViewTodayDiary);
    }

    private void checkYearMonthFormat(int year, int month) {
        try {
            YearMonth.of(year, month);
        } catch (DateTimeException exception) {
            throw new InvalidDateException(
                    ErrorCode.INVALID_DATE,
                    "",
                    String.format("%d-%02d", year, month)
            );
        }
    }
}
