package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.dto.DiaryInMonthly;
import com.exchangediary.diary.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    @Query("""
        SELECT d
        FROM Diary d
        WHERE d.groupMember.member.id = :meberId
    """)
    List<Diary> findByMemberId(Long memberId);

    @Query("""
        SELECT new com.exchangediary.diary.domain.dto.DiaryInMonthly(
            d.id,
            d.createdAt,
            d.groupMember.profileImage
        )
        FROM Diary d
        WHERE d.group.id = :groupId
            AND YEAR(d.createdAt) = :year
            AND MONTH(d.createdAt) = :month
    """)
    List<DiaryInMonthly> findDiaryInMonthlyByGroupIdAndYearAndMonth(String groupId, int year, int month);

    @Query("""
        SELECT d
        FROM Diary d
        WHERE d.group.id = :groupId
            AND CAST(d.createdAt AS DATE) = :date
    """)
    Optional<Diary> findDiaryByGroupIdAndDate(String groupId, LocalDate date);

    @Query("""
        SELECT count(d.id) > 0
        FROM Diary d
        WHERE d.group.id = :groupId
            AND CAST(d.createdAt AS DATE) = date
    """)
    Boolean existsByGroupAndDate(String groupId, LocalDate date);
}
