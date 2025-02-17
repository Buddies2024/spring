package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.dto.DiaryDay;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.ui.dto.response.DiaryWritableStatusResponse;
import com.exchangediary.diary.ui.dto.response.DiaryMonthlyResponse;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.InvalidDateException;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.service.GroupQueryService;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
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
    private final DiaryRepository diaryRepository;
    private final GroupQueryService groupQueryService;
    private final MemberQueryService memberQueryService;

    public Diary findDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.DIARY_NOT_FOUND,
                        "",
                        String.valueOf(diaryId))
                );
    }

    public DiaryResponse viewDiary(Long memberId, Long diaryId) {
        Member member = memberQueryService.findMember(memberId);
        Diary diary = findDiary(diaryId);

        diaryAuthorizationService.checkDiaryViewable(member, diary);

        return DiaryResponse.of(diary);
    }

    public DiaryMonthlyResponse viewMonthlyDiary(int year, int month, String groupId, Long memberId) {
        validateYearMonthFormat(year, month);
        List<DiaryDay> diaries = diaryRepository.findAllByGroupAndYearAndMonth(groupId, year, month);
        LocalDate lastViewableDiaryDate = memberQueryService.getLastViewableDiaryDate(memberId);
        return DiaryMonthlyResponse.of(diaries, lastViewableDiaryDate);
    }

    private void validateYearMonthFormat(int year, int month) {
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

    public DiaryWritableStatusResponse getMembersDiaryAuthorization(String groupId, Long memberId) {
        boolean writtenTodayDiary = false;
        Long diaryId = null;

        Boolean isMyOrder = groupQueryService.isMyOrderInGroup(memberId);
        Optional<Diary> todayDiary = diaryRepository.findTodayDiaryInGroup(groupId);
        if (todayDiary.isPresent()) {
            writtenTodayDiary = true;
            diaryId = getTodayDiaryId(isMyOrder, memberId, todayDiary.get());
        }
        return DiaryWritableStatusResponse.of(isMyOrder, writtenTodayDiary, diaryId);
    }

    private Long getTodayDiaryId(Boolean isMyOrder, Long memberId, Diary todayDiary) {
        if (todayDiary.getMember().getId().equals(memberId)) {
            return todayDiary.getId();
        }
        if (isMyOrder) {
            return todayDiary.getId();
        }
        return null;
    }
}
