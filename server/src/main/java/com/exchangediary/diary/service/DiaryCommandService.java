package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.UploadImageRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.PublicationStatus;
import com.exchangediary.diary.domain.entity.UploadImage;
import com.exchangediary.diary.ui.dto.request.DiaryRequest;
import com.exchangediary.diary.ui.dto.request.UploadImageRequest;
import com.exchangediary.global.domain.StaticImageRepository;
import com.exchangediary.global.domain.entity.ErrorCode;
import com.exchangediary.global.domain.entity.ImageUploadException;
import com.exchangediary.global.domain.entity.NotFoundException;
import com.exchangediary.global.domain.entity.StaticImage;
import com.exchangediary.global.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryCommandService {
    private final DiaryRepository diaryRepository;
    private final StaticImageRepository staticImageRepository;
    private final ImageService imageService;

    public Diary createDiary(DiaryRequest diaryRequest, UploadImageRequest uploadImageRequest) {
        StaticImage moodImage = staticImageRepository.findById(diaryRequest.todayMoodId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.STICKER_IMAGE_NOT_FOUND));
        UploadImage uploadImage = null;
        MultipartFile file = uploadImageRequest.getFile();

        if (file != null && !file.isEmpty()) {
            try {
                uploadImage = imageService.saveUploadImage(file, PublicationStatus.PUBLISHED);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ImageUploadException(ErrorCode.IMAGE_UPLOAD_ERROR, e);
            }
        }
        Diary diary = Diary.of(diaryRequest, moodImage, uploadImage);
        return diaryRepository.save(diary);
    }
}