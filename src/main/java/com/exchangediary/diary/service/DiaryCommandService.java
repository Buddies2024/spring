package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.UploadImage;
import com.exchangediary.diary.ui.dto.request.DiaryRequest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.DuplicateException;
import com.exchangediary.global.exception.serviceexception.FailedImageUploadException;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.service.GroupQueryService;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryCommandService {
    private static final Set<String> VALID_IMAGE_FORMAT = Set.of(
            "image/jpeg",
            "image/gif",
            "image/png",
            "image/heic",
            "image/heif"
    );
    private final DiaryRepository diaryRepository;
    private final MemberQueryService memberQueryService;
    private final GroupQueryService groupQueryService;
    private final GroupRepository groupRepository;

    public Long createDiary(DiaryRequest diaryRequest, MultipartFile file, Long groupId, Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        Group group = groupQueryService.findGroup(groupId);
        checkTodayDiaryExistent(groupId);
        //Todo: 일기 작성 인가 후 삭제
        if (!member.getOrderInGroup().equals(group.getCurrentOrder())) {
            throw new ForbiddenException(
                    ErrorCode.DIARY_WRITE_FORBIDDEN,
                    "",
                    String.valueOf(group.getCurrentOrder())
            );
        }

        if (isEmptyFile(file)) {
            Diary diary = Diary.of(diaryRequest, null);
            changeCurrentOrderOfGroup(group);
            diary.addMemberAndGroup(member, group);
            Diary savedDiary = diaryRepository.save(diary);
            return savedDiary.getId();
        }

        validateImageType(file);
        try {
            UploadImage image = UploadImage.builder()
                    .image(file.getBytes())
                    .build();
            Diary diary = Diary.of(diaryRequest, image);
            changeCurrentOrderOfGroup(group);
            diary.addMemberAndGroup(member, group);
            Diary savedDiary = diaryRepository.save(diary);
            image.uploadToDiary(savedDiary);
            return savedDiary.getId();
        } catch (IOException e) {
            throw new FailedImageUploadException(
                    ErrorCode.FAILED_UPLOAD_IMAGE,
                    "네트워크 오류로 인해 \n일기 업로드에 실패했습니다.\n다시 시도해주세요.",
                    file.getOriginalFilename()
            );
        }
    }

    private void checkTodayDiaryExistent(Long groupId) {
        LocalDate today = LocalDate.now();
        Optional<Long> todayDiary =
                diaryRepository.findIdByGroupAndDate(
                        groupId, today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        if (todayDiary.isPresent()) {
            throw new DuplicateException(
                    ErrorCode.DIARY_DUPLICATED,
                    "",
                    today.toString()
            );
        }
    }

    private boolean isEmptyFile(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    private void changeCurrentOrderOfGroup(Group group) {
        int currentOrder = group.getCurrentOrder() + 1;
        if (group.getMembers().size() < currentOrder)
            currentOrder = 1;
        group.updateCurrentOrder(currentOrder);
        groupRepository.save(group);
    }

    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();

        if (!VALID_IMAGE_FORMAT.contains(contentType)) {
            throw new FailedImageUploadException(
                    ErrorCode.INVALID_IMAGE_FORMAT,
                    "",
                    file.getOriginalFilename()
            );
        }
    }
}
