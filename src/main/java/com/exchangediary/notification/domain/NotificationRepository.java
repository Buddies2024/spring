package com.exchangediary.notification.domain;

import com.exchangediary.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberId(Long memberId);
    @Query("SELECT n.token FROM Notification n JOIN n.member m WHERE m.group.id = :groupId AND m.id != :memberId")
    List<String> findTokensByGroupIdExceptMemberId(String groupId, Long memberId);
    @Query("SELECT n.token FROM Notification n JOIN n.member m WHERE m.group.id = :groupId AND m.id != :memberId AND m.groupRole != 'GROUP_LEADER'")
    List<String> findTokensByGroupIdExceptMemberIdAndLeader(String groupId, Long memberId);
    @Query("SELECT n.token FROM Notification n JOIN n.member m WHERE m.group.id = :groupId AND m.orderInGroup = m.group.currentOrder")
    List<String> findByGroupIdAndCurrentOrder(String groupId);
    @Query("SELECT n.token FROM Notification n " +
            "JOIN n.member m " +
            "JOIN m.group g " +
            "LEFT JOIN Diary d ON d.group = g AND CAST(d.createdAt AS DATE) = CURRENT_DATE " +
            "WHERE m.orderInGroup = m.group.currentOrder AND d.id is NULL")
    List<String> findTokensNoDiaryToday();
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :localDateTime")
    void deleteAllByCreatedAtLessThan(LocalDateTime localDateTime);
    boolean existsByToken(String token);
}
