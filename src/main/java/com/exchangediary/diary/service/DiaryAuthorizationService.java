package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.service.GroupQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryAuthorizationService {
    private final GroupQueryService groupQueryService;
    private final DiaryQueryService diaryQueryService;
    private final DiaryRepository diaryRepository;

    public boolean canWriteDiary(Long memberId, Long groupId) {
        if (!groupQueryService.isSameWithGroupCurrentOrder(memberId)) {
            throw new ForbiddenException(ErrorCode.DIARY_WRITE_FORBIDDEN, "", "");
        }
        if (diaryQueryService.findTodayDiary(groupId).isPresent()) {
            throw new ForbiddenException(ErrorCode.DIARY_WRITE_FORBIDDEN, "", "");
        }
        return true;
    }

    public boolean canViewDiary(Long memberId, Long diaryId) {
        if (!diaryRepository.isViewableDiary(memberId, diaryId)) {
            throw new ForbiddenException(ErrorCode.DIARY_VIEW_FORBIDDEN, "", "");
        }
        return true;
    }
}
