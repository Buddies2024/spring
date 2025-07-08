package com.exchangediary.diary.service;

import com.exchangediary.diary.domain.DiaryRepository;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.FailedImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryImageService {
    private static final Set<String> VALID_IMAGE_FORMAT = Set.of(
            "image/jpeg",
            "image/gif",
            "image/png",
            "image/heic",
            "image/heif"
    );
    private static final String imagePathFormat = "%s/groups/%s";
    private final DiaryRepository diaryRepository;
    @Value("${file.resources.location}")
    private String fileLocation;


    public void saveImage(MultipartFile file, Diary diary, String groupId) throws IOException{
        if (!isEmptyFile(file)) {
            validateImageType(file);
            String imagePath = String.format(imagePathFormat, fileLocation, groupId);

            File directory = new File(imagePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileExtension = getFileExtension(file.getOriginalFilename());
            file.transferTo(new File(imagePath + "/" + date + fileExtension));
            diary.uploadImageFileName(date + fileExtension);
        }
    }

    public void deleteAllImageByMemberId(Long memberId, String groupId) {
        List<Diary> diaries = diaryRepository.findByMemberId(memberId);

        diaries.forEach(diary -> {
            if (diary.getImageFileName() != null) {
                deleteImage(groupId, diary.getImageFileName());
            }
        });
    }

    private boolean isEmptyFile(MultipartFile file) {
        return file == null || file.isEmpty();
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

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex);
    }

    private void deleteImage(String groupId, String fileName) {
        String imageLocation = String.format(imagePathFormat, fileLocation, groupId) + "/" + fileName;

        File file = new File(imageLocation);
        if (file.exists()) {
            file.delete();
        }
    }
}
