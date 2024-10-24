package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.UploadImageRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.UploadImage;
import com.exchangediary.diary.ui.dto.request.DiaryRequest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.FailedImageUploadException;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.service.GroupQueryService;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryWriteService {
    private final MemberQueryService memberQueryService;
    private final GroupQueryService groupQueryService;
    private final DiaryValidationService diaryValidationService;
    private final DiaryRepository diaryRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final UploadImageRepository uploadImageRepository;

    public Long writeDiary(DiaryRequest diaryRequest, MultipartFile file, Long groupId, Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        Group group = groupQueryService.findGroup(groupId);
        diaryValidationService.checkTodayDiaryExistent(groupId);

        try {
            Diary diary = Diary.from(diaryRequest, member, group);

            uploadImage(file, diary);
            updateGroupCurrentOrder(group);
            updateViewableDiaryDate(member, group);
            member.updateLastViewableDiaryDate(LocalDate.now());

            Diary savedDiary = diaryRepository.save(diary);
            return savedDiary.getId();
        } catch (IOException e) {
            throw new FailedImageUploadException(
                    ErrorCode.FAILED_UPLOAD_IMAGE,
                    "네트워크 오류로 인해 \n일기 업로드에 실패했습니다.\n다시 시도해주세요.",
                    file.getOriginalFilename()
            );
        }
    }

    private void uploadImage(MultipartFile file, Diary diary) throws IOException{
        if (!isEmptyFile(file)) {
            diaryValidationService.validateImageType(file);
            uploadImageRepository.save(UploadImage.of(file.getBytes(), diary));
        }
    }

    private boolean isEmptyFile(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    private void updateGroupCurrentOrder(Group group) {
        int currentOrder = group.getCurrentOrder() + 1;
        group.updateCurrentOrder(currentOrder, group.getMembers().size());
        groupRepository.save(group);
    }

    private void updateViewableDiaryDate(Member currentWriter, Group group) {
        Member nextWriter = group.getMembers().stream()
                        .filter(member -> group.getCurrentOrder().equals(member.getOrderInGroup()))
                        .findFirst()
                        .get();

        nextWriter.updateLastViewableDiaryDate(LocalDate.now());
        currentWriter.updateLastViewableDiaryDate(LocalDate.now());
        memberRepository.saveAll(Arrays.asList(currentWriter, nextWriter));
    }
}
