package com.exchangediary.notification.domain;

import com.exchangediary.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByToken(String token);

    @Query("""
        SELECT n.token
        FROM Notification n
        WHERE n.member.onNotification = true
    """)
    List<String> findTokensByMemberId(Long memberId);

    @Query("""
        SELECT n.token
        FROM Notification n
        JOIN n.member m
        JOIN GroupMember gm
            ON gm.member = m
        WHERE gm.group.id = :groupId
            AND m.id <> :memberId
            AND m.onNotification = true
    """)
    List<String> findTokensByGroupIdAndExcludeMemberId(String groupId, Long memberId);

    @Query("""
        SELECT n.token
        FROM Notification n
        JOIN n.member m
        JOIN GroupMember gm
            ON gm.member = m
        WHERE gm.group.id = :groupId
            AND m.id <> :memberId
            AND gm.groupRole <> 'GROUP_LEADER'
            AND m.onNotification = true
    """)
    List<String> findTokensByGroupIdAndExcludeMemberIdAndLeader(String groupId, Long memberId);

    @Query("""
        SELECT n.token
        FROM Notification n
        JOIN n.member m
        JOIN GroupMember gm
            ON gm.member = m
        WHERE gm.group.id = :groupId
            AND gm.orderInGroup = gm.group.currentOrder
            AND m.onNotification = true
    """)
    List<String> findTokensByGroupIdAndCurrentOrder(String groupId);

    @Query("""
        SELECT n.token
        FROM Notification n
        JOIN n.member m
        JOIN GroupMember gm
            ON gm.member = m
        LEFT JOIN Diary d
            ON d.group.id = gm.group.id
            AND CAST(d.createdAt AS DATE) = CURRENT_DATE
        WHERE gm.orderInGroup = gm.group.currentOrder
            AND d.id IS NULL
            AND m.onNotification = true
    """)
    List<String> findTokensByMembersWithoutDiaryToday();

    @Modifying
    @Query("""
        DELETE FROM Notification n
        WHERE n.createdAt < :localDateTime
    """)
    void deleteAllByCreatedAtLessThan(LocalDateTime localDateTime);
}
