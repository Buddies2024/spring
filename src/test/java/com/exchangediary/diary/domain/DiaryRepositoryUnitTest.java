package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.UploadImage;
import com.exchangediary.group.domain.entity.Group;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DiaryRepositoryUnitTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UploadImageRepository uploadImageRepository;

    private byte[] getBinaryImage() {
        try {
            String imageFilePath = "src/test/resources/images/test.jpg";
            return Files.readAllBytes(Paths.get(imageFilePath));
        } catch (IOException ignored) {

        }
        return new byte[0];
    }

    @Test
    void 일기_사진_영속_확인() {
        byte[] image = getBinaryImage();
        UploadImage uploadImage = UploadImage.builder()
                .image(image)
                .build();
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .uploadImage(uploadImage)
                .build();
        entityManager.persist(diary);

        UploadImage updatedUploadImage = uploadImageRepository.findById(uploadImage.getId()).get();

        assertThat(updatedUploadImage.getImage()).isEqualTo(image);
    }

    @Test
    void 일기_사진_삭제_확인() {
        UploadImage uploadImage = UploadImage.builder()
                .image(getBinaryImage())
                .build();
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .uploadImage(uploadImage)
                .build();
        entityManager.persist(diary);

        diaryRepository.delete(diary);

        boolean exist = uploadImageRepository.findById(uploadImage.getId()).isPresent();
        assertThat(exist).isFalse();
    }

    @Test
    void 오늘_일기_있는_경우_조회() {
        Group group = Group.of("group-name", "code");
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .group(group)
                .build();
        entityManager.persist(group);
        entityManager.persist(diary);

        Optional<Diary> result = diaryRepository.findTodayDiaryInGroup(group.getId());

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void 오늘_일기_없는_경우_조회() {
        Group group = Group.of("group-name", "code");
        entityManager.persist(group);

        Optional<Diary> result = diaryRepository.findTodayDiaryInGroup(group.getId());

        assertThat(result.isPresent()).isFalse();
    }
}
