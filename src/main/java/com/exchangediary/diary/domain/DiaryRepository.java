package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.dto.DiaryDay;
import com.exchangediary.diary.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    @Query("SELECT new com.exchangediary.diary.domain.dto.DiaryDay(DAY(d.createdAt), m.profileImage, (m.lastViewableDiaryDate >= CAST(d.createdAt AS DATE))) "+
            "FROM Diary d JOIN d.member m " +
            "ON d.group.id = :groupId AND d.member.id = m.id " +
            "WHERE YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month " +
            "ORDER BY d.createdAt")
    List<DiaryDay> findAllByGroupAndYearAndMonth(Long groupId, int year, int month);
    @Query("SELECT d.id FROM Diary d WHERE d.group.id = :groupId AND CAST(d.createdAt AS DATE) = :date")
    Optional<Long> findIdByGroupAndDate(Long groupId, LocalDate date);
    @Query("SELECT d FROM Diary d WHERE d.group.id = :groupId AND CAST(d.createdAt AS DATE) = CURRENT_DATE")
    Optional<Diary> findTodayDiaryInGroup(Long groupId);
    @Query("SELECT count(d.id) > 0 FROM Diary d WHERE d.group.id = :groupId AND CAST(d.createdAt AS DATE) = CURRENT_DATE")
    Boolean existsTodayDiaryInGroup(Long groupId);
    @Query("SELECT CASE WHEN m.lastViewableDiaryDate >= CAST(d.createdAt AS DATE) THEN true ELSE false END " +
            "FROM Member m JOIN Diary d " +
            "ON m.id = :memberId AND d.id = :diaryId")
    Boolean isViewableDiary(Long memberId,  Long diaryId);
    void deleteByMemberId(Long memberId);
}
