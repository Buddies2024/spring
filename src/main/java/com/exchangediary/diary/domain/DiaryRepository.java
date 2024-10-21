package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    @Query("SELECT d FROM Diary d WHERE d.group.id = :groupId AND YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month ORDER BY d.createdAt")
    List<Diary> findAllByGroupYearAndMonth(Long groupId, int year, int month);
    @Query("SELECT d.id FROM Diary d WHERE d.group.id = :groupId AND YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month AND DAY(d.createdAt) = :day")
    Optional<Long> findIdByGroupAndDate(Long groupId, int year, int month, int day);
    @Query("SELECT d FROM Diary d WHERE d.group.id = :groupId AND YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month AND DAY(d.createdAt) = :day")
    Optional<Diary> findByGroupAndDate(Long groupId, int year, int month, int day);
}
