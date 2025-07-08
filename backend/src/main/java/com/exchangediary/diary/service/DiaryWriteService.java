package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryContentRepository;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.ui.dto.notification.DiaryWriteNotification;
import com.exchangediary.diary.ui.dto.request.DiaryContentRequest;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.diary.ui.dto.request.DiaryRequest;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.FailedImageUploadException;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.GroupMemberRepository;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.group.service.GroupQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryWriteService {
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final DiaryImageService diaryImageService;
    private final GroupQueryService groupQueryService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final DiaryRepository diaryRepository;
    private final DiaryContentRepository diaryContentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public DiaryWriteNotification writeDiary(DiaryRequest diaryRequest, MultipartFile file, String groupId, Long memberId) {
        GroupMember writer = groupMemberQueryService.findGroupMemberByMemberId(memberId);
        Group group = groupQueryService.findGroup(groupId);

        diaryAuthorizationService.checkDiaryWritable(group, writer);

        try {
            Diary diary = Diary.of(diaryRequest.todayMood(), writer, group);
            Diary savedDiary = diaryRepository.save(diary);
            createDairyContent(diaryRequest.contents(), diary);

            diaryImageService.saveImage(file, diary, group.getId());
            updateGroupCurrentOrder(group);
            updateViewableDiaryDate(writer, group);

            return DiaryWriteNotification.from(savedDiary);
        } catch (IOException e) {
            throw new FailedImageUploadException(ErrorCode.FAILED_UPLOAD_IMAGE, "", file.getOriginalFilename());
        }
    }

    private void createDairyContent(List<DiaryContentRequest> contents, Diary diary) {
        List<DiaryContent> diaryContents = new ArrayList<>();
        int index = 0;

        while (index < contents.size()) {
            diaryContents.add(DiaryContent.of(index + 1, contents.get(index).content(), diary));
            index++;
        }
        diaryContentRepository.saveAll(diaryContents);
    }

    private void updateGroupCurrentOrder(Group group) {
        group.changeCurrentOrder(group.getCurrentOrder() + 1);
        groupRepository.save(group);
    }

    private void updateViewableDiaryDate(GroupMember currentWriter, Group group) {
        GroupMember nextWriter = group.getGroupMembers().stream()
                        .filter(member -> group.getCurrentOrder().equals(member.getOrderInGroup()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(
                                ErrorCode.MEMBER_NOT_FOUND,
                                "현재 순서와 일치하는 멤버가 없습니다.",
                                String.valueOf(group.getCurrentOrder())
                        ));

        nextWriter.updateLastViewableDiaryDate();
        currentWriter.updateLastViewableDiaryDate();
        groupMemberRepository.saveAll(Arrays.asList(currentWriter, nextWriter));
    }
}
