package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.UploadImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DiaryRepositoryUnitTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void 일기_사진_연관_관계_확인() throws IOException {
        String imageFilePath = "src/test/resources/images/test.jpg";
        UploadImage uploadImage = UploadImage.builder()
                .image(Files.readAllBytes(Paths.get(imageFilePath)))
                .build();
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .uploadImage(uploadImage)
                .build();
        entityManager.persist(diary);

        Diary result = diaryRepository.getReferenceById(uploadImage.getId());

        assertThat(result.getContent()).isEqualTo("하이하이");
    }
}
