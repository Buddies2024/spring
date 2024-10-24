package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.UploadImage;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DiaryRepositoryUnitTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UploadImageRepository uploadImageRepository;
    @Autowired
    private MemberRepository memberRepository;

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
    void 일기_조회_가능() {
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .build();
        diaryRepository.save(diary);
        Member member = Member.builder()
                .kakaoId(1234L)
                .lastViewableDiaryDate(LocalDate.now())
                .build();
        memberRepository.save(member);

        boolean result = diaryRepository.isViewableDiary(member.getId(), diary.getId());

        assertThat(result).isTrue();
    }

    @Test
    void 일기_조회_불가능() {
        Diary diary = Diary.builder()
                .content("하이하이")
                .moodLocation("/images/write-page/emoji/sleepy.svg")
                .build();
        diaryRepository.save(diary);
        Member member = Member.builder()
                .kakaoId(1234L)
                .lastViewableDiaryDate(LocalDate.now().minusMonths(1))
                .build();
        memberRepository.save(member);

        boolean result = diaryRepository.isViewableDiary(member.getId(), diary.getId());

        assertThat(result).isFalse();
    }
}
