package com.exchangediary.member.domain;

import com.exchangediary.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);

    @Query("""
        SELECT m.onNotification
        FROM Member m
        WHERE m.id = :id
    """)
    Optional<Boolean> findOnNotificationById(Long id);
}
