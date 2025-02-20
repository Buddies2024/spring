package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryImageDeleteService {
    private final ImageService imageService;
    private final DiaryRepository diaryRepository;

    public void deleteImage(Long memberId, String groupId) {
        List<Diary> diaries = diaryRepository.findByMemberId(memberId);

        diaries.forEach(diary -> {
            if (diary.getImageFileName() != null) {
                imageService.deleteImage(groupId, diary.getImageFileName());
            }
        });
    }
}
