package com.exchangediary.comment.domain;

import com.exchangediary.comment.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Boolean existsByDiaryIdAndMemberId(Long memberId, Long diaryId);
}
