package com.exchangediary.notification.service;

import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import com.exchangediary.notification.domain.NotificationRepository;
import com.exchangediary.notification.domain.entity.Notification;
import com.exchangediary.notification.ui.dto.request.NotificationTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationTokenService {
    private final NotificationRepository notificationRepository;
    private final MemberQueryService memberQueryService;

    public List<String> findTokensByMember(Long memberId) {
        return notificationRepository.findTokensByMemberId(memberId);
    }

    public List<String> findTokensByCurrentOrderInGroup(String groupId) {
        return notificationRepository.findTokensByGroupIdAndCurrentOrder(groupId);
    }

    public List<String> findTokensByGroupAndExcludeMember(String groupId, Long memberId) {
        return notificationRepository.findTokensByGroupIdAndExcludeMemberId(groupId, memberId);
    }

    public List<String> findTokensByGroupAndExcludeMemberAndLeader(String groupId, Long memberId) {
        return notificationRepository.findTokensByGroupIdAndExcludeMemberIdAndLeader(groupId, memberId);
    }

    public List<String> findTokensWithoutDiaryToday() {
        return notificationRepository.findTokensByMembersWithoutDiaryToday();
    }

    @Transactional
    public void saveNotificationToken(NotificationTokenRequest notificationTokenRequest, Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        boolean isDuplicated = notificationRepository.existsByToken(notificationTokenRequest.token());

        if (!isDuplicated) {
            Notification notification = Notification.of(notificationTokenRequest.token(), member);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void deleteOldTokens(long expirationDay) {
        notificationRepository.deleteAllByCreatedAtLessThan(LocalDateTime.now().minusDays(expirationDay));
    }
}
