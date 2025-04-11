package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.dto.DiaryInMonthly;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.domain.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class DiaryRepositoryUnitTest {
    private static final String GROUP_NAME = "버디즈";
    private static final String NICKNAME = "스프링";
    private static final String[] PROFILE_IMAGES = {"red", "orange", "yellow", "green", "blue", "navy", "purple"};
    private static final String TODAY_MOOD = "happy";

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DiaryRepository diaryRepository;
    private Member member;
    private Group group;
    private GroupMember groupMember;

    @BeforeEach
    void setup() {
        Member member = Member.from(1L);
        entityManager.persist(member);
        this.member = member;

        Group group = Group.from(GROUP_NAME);
        entityManager.persist(group);
        this.group = group;

        GroupMember groupMember = GroupMember.of(NICKNAME, PROFILE_IMAGES[0], 1, GroupRole.GROUP_LEADER, group, member);
        entityManager.persist(groupMember);
        this.groupMember = groupMember;
    }

    @Test
    @DisplayName("사용자 id로 일기 가져오기")
    public void Test_findByMemberId() {
        // Given
        Diary diary = Diary.of(TODAY_MOOD, groupMember, group);
        entityManager.persist(diary);
        DiaryContent diaryContent = DiaryContent.of(1, "오늘의 날씨 맑음 :)", diary);
        entityManager.persist(diaryContent);

        entityManager.flush();
        entityManager.clear();

        // When
        List<Diary> diaries = diaryRepository.findByMemberId(member.getId());

        // Then
        assertThat(diaries).hasSize(1);
        assertThat(diaries.get(0).getContents()).hasSize(1);
    }

    @Test
    @DisplayName("작성된 일기가 오늘 뿐일 때, 년월별 일기 목록 조회")
    public void Test_findDiaryInMonthlyByGroupIdAndYearAndMonth() {
        // Given
        LocalDate today = LocalDate.now();

        Diary diary = Diary.of(TODAY_MOOD, groupMember, group);
        entityManager.persist(diary);
        DiaryContent diaryContent = DiaryContent.of(1, "오늘의 날씨 맑음 :)", diary);
        entityManager.persist(diaryContent);

        entityManager.flush();
        entityManager.clear();

        // When
        List<DiaryInMonthly> diaries = diaryRepository.findDiaryInMonthlyByGroupIdAndYearAndMonth(group.getId(), today.getYear(), today.getMonthValue());

        // Then
        assertThat(diaries).hasSize(1);

        // When
        diaries = diaryRepository.findDiaryInMonthlyByGroupIdAndYearAndMonth(group.getId(), today.getYear() - 1, today.getMonthValue());

        // Then
        assertThat(diaries).hasSize(0);
    }

    @Test
    @DisplayName("그룹 내 날짜에 해당하는 일기 가져오기")
    public void Test_findDiaryByGroupIdAndDate() {
        // Given
        LocalDate today = LocalDate.now();

        Diary diary = Diary.of(TODAY_MOOD, groupMember, group);
        entityManager.persist(diary);
        DiaryContent diaryContent = DiaryContent.of(1, "오늘의 날씨 맑음 :)", diary);
        entityManager.persist(diaryContent);

        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Diary> maybeDiary = diaryRepository.findDiaryByGroupIdAndDate(group.getId(), today);

        // Then
        assertThat(maybeDiary.isPresent()).isTrue();
        assertThat(maybeDiary.get().getId()).isEqualTo(diary.getId());

        // When
        maybeDiary = diaryRepository.findDiaryByGroupIdAndDate(group.getId(), today.minusDays(1));

        // Then
        assertThat(maybeDiary.isPresent()).isFalse();
    }

    @Test
    @DisplayName("그룹 내 날짜에 작성된 일기 여부 판별")
    public void Test_existsByGroupAndDate() {
        // Given
        LocalDate today = LocalDate.now();

        Diary diary = Diary.of(TODAY_MOOD, groupMember, group);
        entityManager.persist(diary);
        DiaryContent diaryContent = DiaryContent.of(1, "오늘의 날씨 맑음 :)", diary);
        entityManager.persist(diaryContent);

        entityManager.flush();
        entityManager.clear();

        // When
        boolean existsTodayDiary = diaryRepository.existsByGroupAndDate(group.getId(), today);

        // Then
        assertThat(existsTodayDiary).isTrue();

        // When
        boolean existsYesterdayDiary = diaryRepository.existsByGroupAndDate(group.getId(), today.minusDays(1));

        // Then
        assertThat(existsYesterdayDiary).isFalse();
    }
}
