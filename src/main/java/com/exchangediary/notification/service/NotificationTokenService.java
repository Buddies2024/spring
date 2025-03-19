package com.exchangediary.notification.service;

import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import com.exchangediary.notification.domain.NotificationRepository;
import com.exchangediary.notification.domain.entity.Notification;
import com.exchangediary.notification.ui.dto.request.NotificationTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationTokenService {
    private final NotificationRepository notificationRepository;
    private final MemberQueryService memberQueryService;

    @Transactional(readOnly = true)
    public List<String> findTokensByMemberId(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberId(memberId);

        if (notifications.isEmpty()) {
            return new ArrayList<>();
        }
        return notifications.stream()
                .map(Notification::getToken)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findTokensByCurrentOrder(String groupId) {
        return notificationRepository.findByGroupIdAndCurrentOrder(groupId);
    }

    @Transactional(readOnly = true)
    public List<String> findTokensByGroupExceptMember(String groupId, Long memberId) {
        return notificationRepository.findTokensByGroupIdExceptMemberId(groupId, memberId);
    }

    @Transactional(readOnly = true)
    public List<String> findTokensByGroupExceptMemberAndLeader(String groupId, Long memberId) {
        return notificationRepository.findTokensByGroupIdExceptMemberIdAndLeader(groupId, memberId);
    }

    @Transactional(readOnly = true)
    public List<String> findTokensByCurrentOrderInAllGroup() {
        return notificationRepository.findTokensNoDiaryToday();
    }

    public void saveNotificationToken(NotificationTokenRequest notificationTokenRequest, Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        Notification notification = Notification.builder()
                .token(notificationTokenRequest.token())
                .member(member)
                .build();

        try {
            notificationRepository.save(notification);
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    @Transactional
    public void deleteOldTokens(long expirationDay) {
        notificationRepository.deleteAllByCreatedAtLessThan(LocalDateTime.now().minusDays(expirationDay));
    }
}
