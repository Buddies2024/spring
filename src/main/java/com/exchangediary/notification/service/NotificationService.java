package com.exchangediary.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MessageSendService messageSendService;
    private final NotificationTokenService notificationTokenService;

    public void pushNotification(Long memberId, String body) {
        List<String> tokens = notificationTokenService.findTokensByMemberId(memberId);
        messageSendService.sendMulticastMessage(tokens, body);
    }

    public void pushToAllGroupMembers(String groupId, String body) {
        List<String> tokens = notificationTokenService.findTokensByGroup(groupId);
        messageSendService.sendMulticastMessage(tokens, body);
    }

    public void pushToAllGroupMembersExceptMember(String groupId, Long memberId, String body) {
        List<String> tokens = notificationTokenService.findTokensByGroupExceptMember(groupId, memberId);
        messageSendService.sendMulticastMessage(tokens, body);
    }

    public void pushToAllGroupMembersExceptMemberAndLeader(String groupId, Long memberId, String body) {
        List<String> tokens = notificationTokenService.findTokensByGroupExceptMemberAndLeader(groupId, memberId);
        messageSendService.sendMulticastMessage(tokens, body);
    }

    public void pushDiaryOrderNotification(String groupId) {
        List<String> tokens = notificationTokenService.findTokensByCurrentOrder(groupId);
        messageSendService.sendMulticastMessage(tokens, "일기 작성 차례가 되었어요!");
    }

    public void pushSkipOverDiaryNotification(String groupId, int previousOrder) {
        List<String> tokens = notificationTokenService.findTokensByPreviousOrder(groupId, previousOrder);
        messageSendService.sendMulticastMessage(tokens, "방장이 일기 순서를 건너뛰었어요.\n다음 순서를 기다려주세요!");
    }

    public void pushWriteDiaryNotification() {
        List<String> tokens = notificationTokenService.findTokensByCurrentOrderInAllGroup();
        messageSendService.sendMulticastMessage(tokens, "기다리는 친구들을 위해 일기를 작성해주세요!");
    }
}
