package com.exchangediary.group.domain;

import com.exchangediary.group.domain.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("""
        SELECT gm
        FROM GroupMember gm
        WHERE gm.member.id = :memberId
    """)
    Optional<GroupMember> findByMemberId(Long memberId);

    @Query("""
        SELECT gm.group.id
        FROM GroupMember gm
        WHERE gm.member.id = :memberId
    """)
    Optional<String> findGroupIdByMemberId(Long memberId);

    @Query("""
        SELECT gm.lastViewableDiaryDate
        FROM GroupMember gm
        WHERE gm.member.id = :memberId
    """)
    Optional<LocalDate> findLastViewableDiaryDateByMemberId(Long memberId);

    @Query("""
        SELECT COUNT(gm.id) > 0
        FROM GroupMember gm
        WHERE gm.member.id = :memberId
            AND gm.groupRole = 'GROUP_LEADER'
    """)
    boolean isGroupLeaderByMemberId(Long memberId);

    @Query("""
        SELECT
            CASE
                WHEN gm.orderInGroup = gm.group.currentOrder THEN true
                ELSE false
            END
        FROM GroupMember gm
        WHERE gm.member.id = :memberId
    """)
    boolean isCurrentOrderByMemberId(Long memberId);
}
