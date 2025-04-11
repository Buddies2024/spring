package com.exchangediary.diary.domain;

import com.exchangediary.diary.domain.entity.DiaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryContentRepository extends JpaRepository<DiaryContent, Long> {
}
