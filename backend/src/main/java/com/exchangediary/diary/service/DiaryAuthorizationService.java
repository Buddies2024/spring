package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.service.GroupMemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryAuthorizationService {
    private final GroupMemberQueryService groupMemberQueryService;
    private final DiaryRepository diaryRepository;

    public void checkDiaryWritable(Group group, GroupMember groupMember) {
        if (!group.getCurrentOrder().equals(groupMember.getOrderInGroup())) {
            throw new ForbiddenException(ErrorCode.DIARY_WRITE_FORBIDDEN, "", String.valueOf(groupMember.getId()));
        }
        if (diaryRepository.existsByGroupAndDate(group.getId(), LocalDate.now())) {
            throw new ForbiddenException(ErrorCode.DIARY_WRITE_FORBIDDEN, "", LocalDate.now().toString());
        }
    }

    public void checkDiaryWritable(String groupId, Long memberId) {
        if (!groupMemberQueryService.isCurrentOrderInGroup(memberId)) {
            throw new ForbiddenException(ErrorCode.DIARY_WRITE_FORBIDDEN, "", String.valueOf(memberId));
        }
        if (diaryRepository.existsByGroupAndDate(groupId, LocalDate.now())) {
            throw new ForbiddenException(ErrorCode.DIARY_WRITE_FORBIDDEN, "", LocalDate.now().toString());
        }
    }

    public void checkDiaryViewable(LocalDate lastViewableDiaryDate, Diary diary) {
        if (lastViewableDiaryDate.isBefore(diary.getCreatedAt().toLocalDate())) {
            throw new ForbiddenException(ErrorCode.DIARY_VIEW_FORBIDDEN, "", lastViewableDiaryDate.toString());
        }
    }
}
